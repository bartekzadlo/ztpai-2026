package pl.edu.pk.gamelibrary.util;

import org.springframework.stereotype.Component;
import pl.edu.pk.gamelibrary.review.Review;

import java.util.List;

/**
 * Narzędzie do obliczania statystyk ocen gry.
 *
 * Obsługuje zarówno prostą listę liczb (backward-compat),
 * jak i pełnoprawne recenzje z kryteriami.
 *
 * Wagi kryteriów (suma = 1.0):
 *   gameplay     30%
 *   graphics     20%
 *   sound        15%
 *   story        20%
 *   replayValue  15%
 */
@Component
public class RatingCalculator {

    public static final double WEIGHT_GAMEPLAY     = 0.30;
    public static final double WEIGHT_GRAPHICS     = 0.20;
    public static final double WEIGHT_SOUND        = 0.15;
    public static final double WEIGHT_STORY        = 0.20;
    public static final double WEIGHT_REPLAY_VALUE = 0.15;

    // ──────────────────────────────────────────────
    // Proste średnie (backward-compat)
    // ──────────────────────────────────────────────

    /**
     * Oblicza średnią arytmetyczną z listy ocen.
     *
     * @param ratings lista ocen (nie może być null, oceny w zakresie 1–10)
     * @return średnia ocena, 0.0 dla pustej listy
     * @throws IllegalArgumentException gdy ratings == null
     */
    public double calculateAverage(List<Integer> ratings) {
        if (ratings == null) {
            throw new IllegalArgumentException("Lista ocen nie może być null");
        }
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    // ──────────────────────────────────────────────
    // Ważone obliczenia dla recenzji z kryteriami
    // ──────────────────────────────────────────────

    /**
     * Oblicza ważoną ocenę ogólną dla pojedynczej recenzji na podstawie
     * jej kryteriów (gameplay, graphics, sound, story, replayValue).
     *
     * @param review recenzja z wypełnionymi kryteriami (nie może być null)
     * @return ważona ocena ogólna zaokrąglona do 1 miejsca po przecinku
     * @throws IllegalArgumentException gdy review == null lub któreś kryterium jest null
     */
    public double calculateWeightedScore(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Recenzja nie może być null");
        }
        assertScoreNotNull("gameplay",    review.getGameplayScore());
        assertScoreNotNull("graphics",    review.getGraphicsScore());
        assertScoreNotNull("sound",       review.getSoundScore());
        assertScoreNotNull("story",       review.getStoryScore());
        assertScoreNotNull("replayValue", review.getReplayValueScore());

        double weighted =
                review.getGameplayScore()    * WEIGHT_GAMEPLAY     +
                review.getGraphicsScore()    * WEIGHT_GRAPHICS     +
                review.getSoundScore()       * WEIGHT_SOUND        +
                review.getStoryScore()       * WEIGHT_STORY        +
                review.getReplayValueScore() * WEIGHT_REPLAY_VALUE;

        return Math.round(weighted * 10.0) / 10.0;
    }

    /**
     * Oblicza średnią ocenę ogólną ze zbioru recenzji.
     *
     * @param reviews lista recenzji (nie może być null)
     * @return średnia z overallScore, 0.0 dla pustej listy
     * @throws IllegalArgumentException gdy reviews == null
     */
    public double calculateAverageFromReviews(List<Review> reviews) {
        if (reviews == null) {
            throw new IllegalArgumentException("Lista recenzji nie może być null");
        }
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream()
                .mapToDouble(r -> r.getOverallScore() != null ? r.getOverallScore() : 0.0)
                .sum();
        double avg = sum / reviews.size();
        return Math.round(avg * 10.0) / 10.0;
    }

    /**
     * Oblicza średnią dla wybranego kryterium ze zbioru recenzji.
     *
     * @param reviews   lista recenzji (nie może być null)
     * @param criterion kryterium do uśrednienia
     * @return średnia dla kryterium, 0.0 dla pustej listy
     * @throws IllegalArgumentException gdy reviews == null lub criterion == null
     */
    public double calculateCriterionAverage(List<Review> reviews, Criterion criterion) {
        if (reviews == null) {
            throw new IllegalArgumentException("Lista recenzji nie może być null");
        }
        if (criterion == null) {
            throw new IllegalArgumentException("Kryterium nie może być null");
        }
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double avg = reviews.stream()
                .mapToInt(r -> extractScore(r, criterion))
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    // ──────────────────────────────────────────────
    // Pomocnicze
    // ──────────────────────────────────────────────

    private void assertScoreNotNull(String name, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Ocena '" + name + "' nie może być null");
        }
    }

    private int extractScore(Review review, Criterion criterion) {
        return switch (criterion) {
            case GAMEPLAY     -> review.getGameplayScore()    != null ? review.getGameplayScore()    : 0;
            case GRAPHICS     -> review.getGraphicsScore()    != null ? review.getGraphicsScore()    : 0;
            case SOUND        -> review.getSoundScore()       != null ? review.getSoundScore()       : 0;
            case STORY        -> review.getStoryScore()       != null ? review.getStoryScore()       : 0;
            case REPLAY_VALUE -> review.getReplayValueScore() != null ? review.getReplayValueScore() : 0;
        };
    }

    /** Kryteria oceniania dostępne do użycia w {@link #calculateCriterionAverage}. */
    public enum Criterion {
        GAMEPLAY, GRAPHICS, SOUND, STORY, REPLAY_VALUE
    }
}
