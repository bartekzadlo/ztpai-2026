package pl.edu.pk.gamelibrary.review;

import org.springframework.stereotype.Service;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.game.Game;
import pl.edu.pk.gamelibrary.game.GameRepository;
import pl.edu.pk.gamelibrary.review.RatingProfile;
import pl.edu.pk.gamelibrary.review.dto.ReviewRequest;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         GameRepository gameRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    /**
     * Zwraca wszystkie recenzje dla danej gry.
     *
     * @throws ResourceNotFoundException gdy gra nie istnieje
     */
    public List<Review> getReviewsByGame(Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Gra", gameId);
        }
        return reviewRepository.findByGameId(gameId);
    }

    /**
     * Zwraca recenzję po id.
     *
     * @throws ResourceNotFoundException gdy recenzja nie istnieje
     */
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzja", id));
    }

    /**
     * Tworzy nową recenzję gry.
     *
     * Zasady biznesowe:
     *  - gra musi istnieć
     *  - autor musi istnieć
     *  - jeden użytkownik może wystawić tylko jedną recenzję danej gry
     *
     * @throws ResourceNotFoundException  gdy gra lub użytkownik nie istnieje
     * @throws IllegalArgumentException   gdy użytkownik już wystawił recenzję tej gry
     *                                    lub gdy ocena jest poza zakresem 1–10
     */
    public Review createReview(ReviewRequest request, Long authorId) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Gra", request.getGameId()));

        AppUser author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", authorId));

        if (reviewRepository.existsByGameIdAndAuthorId(request.getGameId(), authorId)) {
            throw new IllegalArgumentException(
                    "Użytkownik już wystawił recenzję dla gry o id=" + request.getGameId());
        }

        RatingProfile profile = resolveProfileForCreate(game, request);
        validateScores(game, profile, request);

        Review review = ReviewMapper.toEntity(request);
        review.setGame(game);
        review.setAuthor(author);
        review.setRatingProfile(profile);
        review.recalculateOverallScore();

        return reviewRepository.save(review);
    }

    /**
     * Aktualizuje istniejącą recenzję.
     *
     * Tylko autor recenzji może ją edytować.
     *
     * @throws ResourceNotFoundException  gdy recenzja nie istnieje
     * @throws IllegalArgumentException   gdy próbuje edytować inny użytkownik
     *                                    lub gdy ocena jest poza zakresem 1–10
     */
    public Review updateReview(Long reviewId, ReviewRequest request, Long authorId) {
        Review existing = getReviewById(reviewId);

        if (!existing.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "Brak uprawnień do edycji recenzji id=" + reviewId);
        }

        RatingProfile profile = resolveProfileForUpdate(existing, request);
        validateScores(existing.getGame(), profile, request);

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setGameplayScore(request.getGameplayScore());
        existing.setGraphicsScore(request.getGraphicsScore());
        existing.setSoundScore(request.getSoundScore());
        existing.setStoryScore(request.getStoryScore());
        existing.setReplayValueScore(request.getReplayValueScore());
        existing.setRatingProfile(profile);
        existing.recalculateOverallScore();

        return reviewRepository.save(existing);
    }

    /**
     * Usuwa recenzję.
     *
     * Tylko autor może usunąć własną recenzję.
     *
     * @throws ResourceNotFoundException  gdy recenzja nie istnieje
     * @throws IllegalArgumentException   gdy próbuje usunąć inny użytkownik
     */
    public void deleteReview(Long reviewId, Long authorId) {
        Review existing = getReviewById(reviewId);

        if (!existing.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "Brak uprawnień do usunięcia recenzji id=" + reviewId);
        }

        reviewRepository.deleteById(reviewId);
    }

    /**
     * Zwraca średnią ocenę ogólną gry na podstawie wszystkich recenzji.
     *
     * @return średnia lub 0.0 gdy brak recenzji
     * @throws ResourceNotFoundException gdy gra nie istnieje
     */
    public double getAverageScoreForGame(Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Gra", gameId);
        }
        return reviewRepository.findAverageOverallScoreByGameId(gameId)
                .orElse(0.0);
    }

    // ──────────────────────────────────────────────
    // Pomocnicze
    // ──────────────────────────────────────────────

    private RatingProfile resolveProfileForCreate(Game game, ReviewRequest req) {
        if (req.getRatingProfile() != null) {
            return req.getRatingProfile();
        }
        if (game.getDefaultRatingProfile() != null) {
            return game.getDefaultRatingProfile();
        }
        return RatingProfile.DEFAULT;
    }

    private RatingProfile resolveProfileForUpdate(Review existing, ReviewRequest req) {
        if (req.getRatingProfile() != null) {
            return req.getRatingProfile();
        }
        if (existing.getRatingProfile() != null) {
            return existing.getRatingProfile();
        }
        Game game = existing.getGame();
        return game != null && game.getDefaultRatingProfile() != null
                ? game.getDefaultRatingProfile()
                : RatingProfile.DEFAULT;
    }

    private void validateScores(Game game, RatingProfile profile, ReviewRequest req) {
        validateScoreRequired("gameplay",    req.getGameplayScore());
        validateScoreRequired("graphics",    req.getGraphicsScore());
        validateScoreRequired("sound",       req.getSoundScore());
        validateScoreRequired("replayValue", req.getReplayValueScore());

        boolean gameHasStory = game != null && game.isHasStory();
        boolean storyRequired = gameHasStory && profile == RatingProfile.NARRATIVE;
        if (storyRequired) {
            validateScoreRequired("story", req.getStoryScore());
        } else {
            validateScoreOptional("story", req.getStoryScore());
        }
    }

    private void validateScoreRequired(String name, Integer value) {
        if (value == null || value < 1 || value > 10) {
            throw new IllegalArgumentException(
                    "Ocena '" + name + "' musi być w zakresie 1–10, otrzymano: " + value);
        }
    }

    private void validateScoreOptional(String name, Integer value) {
        if (value == null) return;
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException(
                    "Ocena '" + name + "' musi być w zakresie 1–10, otrzymano: " + value);
        }
    }
}
