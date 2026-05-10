package pl.edu.pk.gamelibrary.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.review.RatingProfile;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Page<Game> searchGames(GameSearchCriteria criteria, Pageable pageable) {
        return gameRepository.findAll(GameSpecifications.byCriteria(criteria), pageable);
    }

    /**
     * Zwraca grę lub rzuca ResourceNotFoundException (-> 404).
     */
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gra", id));
    }

    public Game createGame(Game game) {
        if (game.getTitle() == null || game.getTitle().isBlank()) {
            throw new IllegalArgumentException("Tytuł gry nie może być null ani pusty");
        }
        // Bezpieczne domyślne wartości dla pól opcjonalnych w żądaniu.
        if (game.getDefaultRatingProfile() == null) {
            game.setDefaultRatingProfile(RatingProfile.DEFAULT);
        }
        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game updated) {
        Game existing = getGameById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setGenre(updated.getGenre());
        existing.setPlatform(updated.getPlatform());
        existing.setReleaseYear(updated.getReleaseYear());
        existing.setCoverUrl(updated.getCoverUrl());
        existing.setHasStory(updated.isHasStory());
        existing.setDefaultRatingProfile(updated.getDefaultRatingProfile());
        return gameRepository.save(existing);
    }

    public void deleteGame(Long id) {
        getGameById(id); // rzuci 404 jeśli brak
        gameRepository.deleteById(id);
    }
}
