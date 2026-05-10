package pl.edu.pk.gamelibrary.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.auth.model.Role;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.game.Game;
import pl.edu.pk.gamelibrary.game.GameRepository;
import pl.edu.pk.gamelibrary.review.dto.ReviewRequest;
import pl.edu.pk.gamelibrary.review.dto.GameRatingStatsResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla ReviewService.
 *
 * Pokryte przypadki:
 *  - getReviewsByGame  : happy path, nieistniejąca gra
 *  - getReviewById     : happy path, nieistniejąca recenzja
 *  - createReview      : happy path, duplikat recenzji, brak gry, brak użytkownika,
 *                        nieprawidłowa ocena (poza zakresem 1–10)
 *  - updateReview      : happy path, brak uprawnień, nieistniejąca recenzja, nieprawidłowa ocena
 *  - deleteReview      : happy path, brak uprawnień, nieistniejąca recenzja
 *  - getAverageScoreForGame : happy path (z recenzjami), brak recenzji, nieistniejąca gra
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService – testy jednostkowe")
class ReviewServiceTest {

    // ──────────────────────────────────────────────
    // Mocki
    // ──────────────────────────────────────────────

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    // ================================================================ getReviewsByGame

    @Nested
    @DisplayName("getReviewsByGame")
    class GetReviewsByGame {

        @Test
        @DisplayName("zwraca listę recenzji gdy gra istnieje")
        void getReviewsByGame_shouldReturnList_whenGameExists() {
            // given
            when(gameRepository.existsById(1L)).thenReturn(true);
            Review r1 = buildReview(buildGame(1L, "The Witcher 3"), buildUser(10L, "jan"), 8, 7, 9, 10, 8);
            when(reviewRepository.findByGameId(1L)).thenReturn(List.of(r1));

            // when
            List<Review> result = reviewService.getReviewsByGame(1L);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(gameRepository, times(1)).existsById(1L);
            verify(reviewRepository, times(1)).findByGameId(1L);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy gra nie istnieje")
        void getReviewsByGame_shouldThrow_whenGameNotFound() {
            // given
            when(gameRepository.existsById(99L)).thenReturn(false);

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.getReviewsByGame(99L),
                    "Powinien zostać rzucony ResourceNotFoundException gdy gra nie istnieje");

            verify(reviewRepository, never()).findByGameId(any());
        }
    }

    // ================================================================ getReviewById

    @Nested
    @DisplayName("getReviewById")
    class GetReviewById {

        @Test
        @DisplayName("zwraca recenzję gdy istnieje")
        void getReviewById_shouldReturnReview_whenFound() {
            // given
            Review r = buildReview(buildGame(1L, "Elden Ring"), buildUser(10L, "jan"), 9, 10, 8, 7, 9);
            setId(r, 5L);
            when(reviewRepository.findById(5L)).thenReturn(Optional.of(r));

            // when
            Review result = reviewService.getReviewById(5L);

            // then
            assertNotNull(result);
            verify(reviewRepository, times(1)).findById(5L);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy recenzja nie istnieje")
        void getReviewById_shouldThrow_whenNotFound() {
            // given
            when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

            // when / then
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> reviewService.getReviewById(99L));

            assertTrue(ex.getMessage().contains("99"));
        }
    }

    // ================================================================ createReview

    @Nested
    @DisplayName("createReview")
    class CreateReview {

        @Test
        @DisplayName("tworzy recenzję i zwraca zapisany obiekt")
        void createReview_shouldSaveAndReturnReview() {
            // given
            Game game     = buildGame(1L, "Dark Souls");
            AppUser author = buildUser(10L, "gracze123");
            ReviewRequest req = buildRequest(1L, 8, 7, 9, 6, 8);

            when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(reviewRepository.existsByGameIdAndAuthorId(1L, 10L)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
                Review saved = inv.getArgument(0);
                setId(saved, 42L);
                return saved;
            });

            // when
            Review result = reviewService.createReview(req, 10L);

            // then
            assertNotNull(result);
            assertEquals(42L, result.getId());
            assertEquals(game, result.getGame());
            assertEquals(author, result.getAuthor());
            assertEquals(8, result.getGameplayScore());
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy gra nie istnieje")
        void createReview_shouldThrow_whenGameNotFound() {
            // given
            ReviewRequest req = buildRequest(99L, 8, 8, 8, 8, 8);
            when(gameRepository.findById(99L)).thenReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.createReview(req, 10L));

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy użytkownik nie istnieje")
        void createReview_shouldThrow_whenAuthorNotFound() {
            // given
            Game game = buildGame(1L, "Doom");
            ReviewRequest req = buildRequest(1L, 8, 8, 8, 8, 8);

            when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.createReview(req, 99L));

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy użytkownik już wystawił recenzję tej gry")
        void createReview_shouldThrow_whenDuplicateReview() {
            // given
            Game game     = buildGame(1L, "Hades");
            AppUser author = buildUser(10L, "ktoś");
            ReviewRequest req = buildRequest(1L, 8, 8, 8, 8, 8);

            when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(reviewRepository.existsByGameIdAndAuthorId(1L, 10L)).thenReturn(true);

            // when / then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> reviewService.createReview(req, 10L));

            assertTrue(ex.getMessage().contains("już wystawił"),
                    "Komunikat powinien informować o duplikacie recenzji");
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy gameplayScore == 0 (poza zakresem)")
        void createReview_shouldThrow_whenGameplayScoreIsZero() {
            // given
            Game game     = buildGame(1L, "Celeste");
            AppUser author = buildUser(10L, "ktoś");
            ReviewRequest req = buildRequest(1L, 0, 8, 8, 8, 8); // gameplay=0 – niedozwolone

            when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(reviewRepository.existsByGameIdAndAuthorId(1L, 10L)).thenReturn(false);

            // when / then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> reviewService.createReview(req, 10L));

            assertTrue(ex.getMessage().contains("gameplay"),
                    "Komunikat powinien wskazywać które kryterium jest nieprawidłowe");
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy graphicsScore == 11 (poza zakresem)")
        void createReview_shouldThrow_whenGraphicsScoreExceedsMax() {
            // given
            Game game     = buildGame(1L, "RDR2");
            AppUser author = buildUser(10L, "ktoś");
            ReviewRequest req = buildRequest(1L, 8, 11, 8, 8, 8); // graphics=11 – niedozwolone

            when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(reviewRepository.existsByGameIdAndAuthorId(1L, 10L)).thenReturn(false);

            // when / then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> reviewService.createReview(req, 10L));

            assertTrue(ex.getMessage().contains("graphics"));
            verify(reviewRepository, never()).save(any());
        }
    }

    // ================================================================ updateReview

    @Nested
    @DisplayName("updateReview")
    class UpdateReview {

        @Test
        @DisplayName("aktualizuje recenzję gdy autor ma uprawnienia")
        void updateReview_shouldUpdate_whenAuthorMatches() {
            // given
            AppUser author = buildUser(10L, "jan");
            Game game = buildGame(1L, "Portal 2");
            Review existing = buildReview(game, author, 7, 7, 7, 7, 7);
            setId(existing, 5L);

            ReviewRequest req = buildRequest(1L, 9, 9, 9, 9, 9);
            req.setTitle("Zaktualizowany tytuł");
            req.setContent("Zaktualizowana treść recenzji – minimum 10 znaków.");

            when(reviewRepository.findById(5L)).thenReturn(Optional.of(existing));
            when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Review result = reviewService.updateReview(5L, req, 10L);

            // then
            assertEquals(9, result.getGameplayScore());
            assertEquals(9, result.getGraphicsScore());
            assertEquals("Zaktualizowany tytuł", result.getTitle());
            verify(reviewRepository, times(1)).save(existing);
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy próbuje edytować inny użytkownik")
        void updateReview_shouldThrow_whenAuthorMismatch() {
            // given
            AppUser originalAuthor = buildUser(10L, "jan");
            Review existing = buildReview(buildGame(1L, "Gra"), originalAuthor, 7, 7, 7, 7, 7);
            setId(existing, 5L);

            when(reviewRepository.findById(5L)).thenReturn(Optional.of(existing));

            // when / then
            assertThrows(IllegalArgumentException.class,
                    () -> reviewService.updateReview(5L, buildRequest(1L, 8, 8, 8, 8, 8), 99L),
                    "Powinien zostać rzucony IllegalArgumentException gdy inny użytkownik próbuje edytować");

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy recenzja nie istnieje")
        void updateReview_shouldThrow_whenReviewNotFound() {
            // given
            when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.updateReview(99L, buildRequest(1L, 8, 8, 8, 8, 8), 10L));

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy soundScore == null")
        void updateReview_shouldThrow_whenSoundScoreIsNull() {
            // given
            AppUser author = buildUser(10L, "jan");
            Review existing = buildReview(buildGame(1L, "Gra"), author, 7, 7, 7, 7, 7);
            setId(existing, 5L);

            ReviewRequest req = buildRequest(1L, 8, 8, null, 8, 8); // sound=null
            when(reviewRepository.findById(5L)).thenReturn(Optional.of(existing));

            // when / then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> reviewService.updateReview(5L, req, 10L));

            assertTrue(ex.getMessage().contains("sound"));
            verify(reviewRepository, never()).save(any());
        }
    }

    // ================================================================ deleteReview

    @Nested
    @DisplayName("deleteReview")
    class DeleteReview {

        @Test
        @DisplayName("usuwa recenzję gdy autor ma uprawnienia")
        void deleteReview_shouldDelete_whenAuthorMatches() {
            // given
            AppUser author = buildUser(10L, "jan");
            Review existing = buildReview(buildGame(1L, "Gra"), author, 8, 8, 8, 8, 8);
            setId(existing, 5L);

            when(reviewRepository.findById(5L)).thenReturn(Optional.of(existing));

            // when / then
            assertDoesNotThrow(() -> reviewService.deleteReview(5L, 10L));
            verify(reviewRepository, times(1)).deleteById(5L);
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy próbuje usunąć inny użytkownik")
        void deleteReview_shouldThrow_whenAuthorMismatch() {
            // given
            AppUser author = buildUser(10L, "jan");
            Review existing = buildReview(buildGame(1L, "Gra"), author, 8, 8, 8, 8, 8);
            setId(existing, 5L);

            when(reviewRepository.findById(5L)).thenReturn(Optional.of(existing));

            // when / then
            assertThrows(IllegalArgumentException.class,
                    () -> reviewService.deleteReview(5L, 77L));

            verify(reviewRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy recenzja nie istnieje")
        void deleteReview_shouldThrow_whenReviewNotFound() {
            // given
            when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.deleteReview(99L, 10L));

            verify(reviewRepository, never()).deleteById(any());
        }
    }

    // ================================================================ getAverageScoreForGame

    @Nested
    @DisplayName("getAverageScoreForGame")
    class GetAverageScoreForGame {

        @Test
        @DisplayName("zwraca średnią gdy gra posiada recenzje")
        void getAverageScore_shouldReturnAverage_whenReviewsExist() {
            // given
            when(gameRepository.existsById(1L)).thenReturn(true);
            when(reviewRepository.findAverageOverallScoreByGameId(1L))
                    .thenReturn(Optional.of(8.5));

            // when
            double result = reviewService.getAverageScoreForGame(1L);

            // then
            assertEquals(8.5, result, 0.001);
        }

        @Test
        @DisplayName("zwraca 0.0 gdy gra nie ma żadnych recenzji")
        void getAverageScore_shouldReturnZero_whenNoReviews() {
            // given
            when(gameRepository.existsById(1L)).thenReturn(true);
            when(reviewRepository.findAverageOverallScoreByGameId(1L))
                    .thenReturn(Optional.empty());

            // when
            double result = reviewService.getAverageScoreForGame(1L);

            // then
            assertEquals(0.0, result, 0.001);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy gra nie istnieje")
        void getAverageScore_shouldThrow_whenGameNotFound() {
            // given
            when(gameRepository.existsById(99L)).thenReturn(false);

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.getAverageScoreForGame(99L));

            verify(reviewRepository, never()).findAverageOverallScoreByGameId(any());
        }
    }

    // ================================================================ getRatingStatsForGame

    @Nested
    @DisplayName("getRatingStatsForGame")
    class GetRatingStatsForGame {

        @Test
        @DisplayName("zwraca statystyki (count/avg/histogram) gdy gra istnieje")
        void getRatingStats_shouldReturnStats() {
            // given
            when(gameRepository.existsById(1L)).thenReturn(true);

            Review r1 = buildReview(buildGame(1L, "G1"), buildUser(10L, "u1"), 10, 10, 10, 10, 10); // overall ~10
            Review r2 = buildReview(buildGame(1L, "G1"), buildUser(11L, "u2"), 8, 8, 8, 8, 8);       // overall ~8
            when(reviewRepository.findByGameId(1L)).thenReturn(List.of(r1, r2));

            // when
            GameRatingStatsResponse stats = reviewService.getRatingStatsForGame(1L);

            // then
            assertNotNull(stats);
            assertEquals(1L, stats.getGameId());
            assertEquals(2, stats.getRatingCount());
            assertTrue(stats.getAverageOverallScore() > 0.0);

            Map<Integer, Long> hist = stats.getOverallScoreHistogram();
            assertNotNull(hist);
            assertEquals(10, hist.size(), "Histogram powinien zawierać klucze 1..10");
            assertEquals(1L, hist.get(10));
            assertEquals(1L, hist.get(8));
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy gra nie istnieje")
        void getRatingStats_shouldThrow_whenGameNotFound() {
            // given
            when(gameRepository.existsById(99L)).thenReturn(false);

            // when / then
            assertThrows(ResourceNotFoundException.class,
                    () -> reviewService.getRatingStatsForGame(99L));
            verify(reviewRepository, never()).findByGameId(any());
        }
    }

    // ──────────────────────────────────────────────
    // Pomocnicze fabryki
    // ──────────────────────────────────────────────

    private static Game buildGame(Long id, String title) {
        Game g = new Game(title, "opis", "RPG", "PC", 2020, null);
        setGameId(g, id);
        return g;
    }

    private static AppUser buildUser(Long id, String username) {
        AppUser u = new AppUser(username, "$hash$", Role.USER);
        setUserId(u, id);
        return u;
    }

    private static Review buildReview(Game game, AppUser author,
                                      int gameplay, int graphics, int sound,
                                      int story, int replay) {
        Review r = new Review();
        r.setGame(game);
        r.setAuthor(author);
        r.setTitle("Testowa recenzja");
        r.setContent("Treść testowej recenzji – co najmniej 10 znaków.");
        r.setGameplayScore(gameplay);
        r.setGraphicsScore(graphics);
        r.setSoundScore(sound);
        r.setStoryScore(story);
        r.setReplayValueScore(replay);
        r.recalculateOverallScore();
        return r;
    }

    private static ReviewRequest buildRequest(Long gameId,
                                              Integer gameplay, Integer graphics,
                                              Integer sound, Integer story, Integer replay) {
        ReviewRequest req = new ReviewRequest();
        req.setGameId(gameId);
        req.setTitle("Testowy tytuł recenzji");
        req.setContent("Treść testowa o długości powyżej 10 znaków.");
        req.setGameplayScore(gameplay);
        req.setGraphicsScore(graphics);
        req.setSoundScore(sound);
        req.setStoryScore(story);
        req.setReplayValueScore(replay);
        return req;
    }

    private static void setId(Review review, Long id) {
        try {
            var field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, id);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się ustawić id recenzji przez refleksję", e);
        }
    }

    private static void setGameId(Game game, Long id) {
        try {
            var field = Game.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(game, id);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się ustawić id gry przez refleksję", e);
        }
    }

    private static void setUserId(AppUser user, Long id) {
        try {
            var field = AppUser.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się ustawić id użytkownika przez refleksję", e);
        }
    }
}
