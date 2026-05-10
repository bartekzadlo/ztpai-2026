package pl.edu.pk.gamelibrary.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.edu.pk.gamelibrary.review.Review;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla RatingCalculator.
 *
 * Pokryte przypadki:
 *  - calculateAverage (lista prostych ocen) – happy path, pusta lista, null
 *  - calculateWeightedScore (pojedyncza recenzja) – poprawne wagi, null review, null kryterium
 *  - calculateAverageFromReviews – happy path, pusta lista, null
 *  - calculateCriterionAverage – każde kryterium, pusta lista, null
 */
@DisplayName("RatingCalculator – testy jednostkowe")
class RatingCalculatorTest {

    private RatingCalculator ratingCalculator;

    @BeforeEach
    void setUp() {
        ratingCalculator = new RatingCalculator();
    }

    // ================================================================ calculateAverage

    @Nested
    @DisplayName("calculateAverage (lista Integer)")
    class CalculateAverage {

        @Test
        @DisplayName("zwraca 4.0 dla ocen [3, 4, 5]")
        void calculateAverage_shouldReturnCorrectAverage() {
            List<Integer> ratings = List.of(3, 4, 5);

            double average = ratingCalculator.calculateAverage(ratings);

            assertEquals(4.0, average, 0.001,
                    "Średnia ocen [3, 4, 5] powinna wynosić 4.0");
        }

        @Test
        @DisplayName("zwraca 0.0 dla pustej listy")
        void calculateAverage_shouldReturnZeroForEmptyList() {
            double average = ratingCalculator.calculateAverage(Collections.emptyList());

            assertEquals(0.0, average, 0.001,
                    "Średnia dla pustej listy powinna wynosić 0.0");
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy argument to null")
        void calculateAverage_shouldThrowWhenRatingsIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateAverage(null),
                    "Powinien zostać rzucony IllegalArgumentException dla null");
        }

        @Test
        @DisplayName("zwraca poprawną średnią dla jednoelementowej listy")
        void calculateAverage_shouldHandleSingleElement() {
            assertEquals(7.0, ratingCalculator.calculateAverage(List.of(7)), 0.001);
        }
    }

    // ================================================================ calculateWeightedScore

    @Nested
    @DisplayName("calculateWeightedScore (pojedyncza recenzja)")
    class CalculateWeightedScore {

        /**
         * Weryfikacja wag:
         *   gameplay=10 * 0.30 = 3.0
         *   graphics=10 * 0.20 = 2.0
         *   sound=10   * 0.15 = 1.5
         *   story=10   * 0.20 = 2.0
         *   replay=10  * 0.15 = 1.5
         *   suma = 10.0
         */
        @Test
        @DisplayName("zwraca 10.0 gdy wszystkie kryteria == 10")
        void calculateWeightedScore_shouldReturn10_whenAllScoresAre10() {
            Review review = buildReview(10, 10, 10, 10, 10);

            double result = ratingCalculator.calculateWeightedScore(review);

            assertEquals(10.0, result, 0.001);
        }

        /**
         * Weryfikacja wag:
         *   gameplay=1 * 0.30 = 0.30
         *   graphics=1 * 0.20 = 0.20
         *   sound=1   * 0.15 = 0.15
         *   story=1   * 0.20 = 0.20
         *   replay=1  * 0.15 = 0.15
         *   suma = 1.0
         */
        @Test
        @DisplayName("zwraca 1.0 gdy wszystkie kryteria == 1")
        void calculateWeightedScore_shouldReturn1_whenAllScoresAre1() {
            Review review = buildReview(1, 1, 1, 1, 1);

            double result = ratingCalculator.calculateWeightedScore(review);

            assertEquals(1.0, result, 0.001);
        }

        /**
         * gameplay=8  * 0.30 = 2.4
         * graphics=6  * 0.20 = 1.2
         * sound=7     * 0.15 = 1.05
         * story=9     * 0.20 = 1.8
         * replay=5    * 0.15 = 0.75
         * suma = 7.2
         */
        @Test
        @DisplayName("poprawnie oblicza ważoną ocenę dla różnych kryteriów")
        void calculateWeightedScore_shouldComputeCorrectWeightedScore() {
            Review review = buildReview(8, 6, 7, 9, 5);

            double result = ratingCalculator.calculateWeightedScore(review);

            assertEquals(7.2, result, 0.001);
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy review jest null")
        void calculateWeightedScore_shouldThrowWhenReviewIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateWeightedScore(null),
                    "Powinien zostać rzucony IllegalArgumentException dla null");
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy gameplayScore jest null")
        void calculateWeightedScore_shouldThrowWhenGameplayScoreIsNull() {
            Review review = buildReview(null, 8, 8, 8, 8);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateWeightedScore(review));

            assertTrue(ex.getMessage().contains("gameplay"));
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy graphicsScore jest null")
        void calculateWeightedScore_shouldThrowWhenGraphicsScoreIsNull() {
            Review review = buildReview(8, null, 8, 8, 8);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateWeightedScore(review));

            assertTrue(ex.getMessage().contains("graphics"));
        }
    }

    // ================================================================ calculateAverageFromReviews

    @Nested
    @DisplayName("calculateAverageFromReviews")
    class CalculateAverageFromReviews {

        @Test
        @DisplayName("zwraca poprawną średnią z kilku recenzji")
        void calculateAverageFromReviews_shouldReturnCorrectAverage() {
            Review r1 = buildReviewWithOverall(8.0);
            Review r2 = buildReviewWithOverall(6.0);

            double result = ratingCalculator.calculateAverageFromReviews(List.of(r1, r2));

            assertEquals(7.0, result, 0.001);
        }

        @Test
        @DisplayName("zwraca 0.0 dla pustej listy")
        void calculateAverageFromReviews_shouldReturnZeroForEmptyList() {
            double result = ratingCalculator.calculateAverageFromReviews(Collections.emptyList());

            assertEquals(0.0, result, 0.001);
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy lista jest null")
        void calculateAverageFromReviews_shouldThrowWhenNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateAverageFromReviews(null));
        }
    }

    // ================================================================ calculateCriterionAverage

    @Nested
    @DisplayName("calculateCriterionAverage")
    class CalculateCriterionAverage {

        @Test
        @DisplayName("oblicza średnią gameplay dla dwóch recenzji")
        void calculateCriterionAverage_shouldComputeGameplayAverage() {
            Review r1 = buildReview(8, 5, 5, 5, 5);
            Review r2 = buildReview(6, 5, 5, 5, 5);

            double result = ratingCalculator.calculateCriterionAverage(
                    List.of(r1, r2), RatingCalculator.Criterion.GAMEPLAY);

            assertEquals(7.0, result, 0.001);
        }

        @Test
        @DisplayName("oblicza średnią graphics dla dwóch recenzji")
        void calculateCriterionAverage_shouldComputeGraphicsAverage() {
            Review r1 = buildReview(5, 10, 5, 5, 5);
            Review r2 = buildReview(5, 4, 5, 5, 5);

            double result = ratingCalculator.calculateCriterionAverage(
                    List.of(r1, r2), RatingCalculator.Criterion.GRAPHICS);

            assertEquals(7.0, result, 0.001);
        }

        @Test
        @DisplayName("oblicza średnią sound dla dwóch recenzji")
        void calculateCriterionAverage_shouldComputeSoundAverage() {
            Review r1 = buildReview(5, 5, 9, 5, 5);
            Review r2 = buildReview(5, 5, 3, 5, 5);

            double result = ratingCalculator.calculateCriterionAverage(
                    List.of(r1, r2), RatingCalculator.Criterion.SOUND);

            assertEquals(6.0, result, 0.001);
        }

        @Test
        @DisplayName("oblicza średnią story dla dwóch recenzji")
        void calculateCriterionAverage_shouldComputeStoryAverage() {
            Review r1 = buildReview(5, 5, 5, 7, 5);
            Review r2 = buildReview(5, 5, 5, 5, 5);

            double result = ratingCalculator.calculateCriterionAverage(
                    List.of(r1, r2), RatingCalculator.Criterion.STORY);

            assertEquals(6.0, result, 0.001);
        }

        @Test
        @DisplayName("oblicza średnią replayValue dla dwóch recenzji")
        void calculateCriterionAverage_shouldComputeReplayValueAverage() {
            Review r1 = buildReview(5, 5, 5, 5, 10);
            Review r2 = buildReview(5, 5, 5, 5, 2);

            double result = ratingCalculator.calculateCriterionAverage(
                    List.of(r1, r2), RatingCalculator.Criterion.REPLAY_VALUE);

            assertEquals(6.0, result, 0.001);
        }

        @Test
        @DisplayName("zwraca 0.0 dla pustej listy")
        void calculateCriterionAverage_shouldReturnZeroForEmptyList() {
            double result = ratingCalculator.calculateCriterionAverage(
                    Collections.emptyList(), RatingCalculator.Criterion.GAMEPLAY);

            assertEquals(0.0, result, 0.001);
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy lista jest null")
        void calculateCriterionAverage_shouldThrowWhenReviewsIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateCriterionAverage(
                            null, RatingCalculator.Criterion.GAMEPLAY));
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy criterion jest null")
        void calculateCriterionAverage_shouldThrowWhenCriterionIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> ratingCalculator.calculateCriterionAverage(
                            Collections.emptyList(), null));
        }
    }

    // ──────────────────────────────────────────────
    // Pomocnicze fabryki
    // ──────────────────────────────────────────────

    private static Review buildReview(Integer gameplay, Integer graphics,
                                      Integer sound, Integer story, Integer replay) {
        Review r = new Review();
        r.setGameplayScore(gameplay);
        r.setGraphicsScore(graphics);
        r.setSoundScore(sound);
        r.setStoryScore(story);
        r.setReplayValueScore(replay);
        return r;
    }

    /** Tworzy recenzję z ustawionym overallScore przez refleksję (symulacja @PrePersist). */
    private static Review buildReviewWithOverall(double overall) {
        Review r = buildReview(5, 5, 5, 5, 5);
        try {
            var field = Review.class.getDeclaredField("overallScore");
            field.setAccessible(true);
            field.set(r, overall);
        } catch (Exception e) {
            throw new RuntimeException("Nie można ustawić overallScore przez refleksję", e);
        }
        return r;
    }
}
