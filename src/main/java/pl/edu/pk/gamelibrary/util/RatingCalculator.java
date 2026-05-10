package pl.edu.pk.gamelibrary.util;

import org.springframework.stereotype.Component;
import pl.edu.pk.gamelibrary.review.RatingProfile;
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

    // Alternatywne profile wag dopasowane do istniejącego modelu (5 pól).
    private static final double NARR_WEIGHT_GAMEPLAY     = 0.30;
    private static final double NARR_WEIGHT_GRAPHICS     = 0.15;
    private static final double NARR_WEIGHT_SOUND        = 0.15;
    private static final double NARR_WEIGHT_STORY        = 0.30;
    private static final double NARR_WEIGHT_REPLAY_VALUE = 0.10;

    private static final double MP_WEIGHT_GAMEPLAY     = 0.40;
    private static final double MP_WEIGHT_GRAPHICS     = 0.20;
    private static final double MP_WEIGHT_SOUND        = 0.15;
    private static final double MP_WEIGHT_STORY        = 0.00;
    private static final double MP_WEIGHT_REPLAY_VALUE = 0.25;

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
        return calculateWeightedScore(review, review != null ? review.getRatingProfile() : null);
    }

    /**
     * Oblicza ważoną ocenę ogólną z uwzględnieniem profilu oraz kryteriów N/A (null).
     *
     * Zasada: kryteria o wartości null są pomijane, a wagi pozostałych są renormalizowane.
     */
    public double calculateWeightedScore(Review review, RatingProfile profile) {
        if (review == null) {
            throw new IllegalArgumentException("Recenzja nie może być null");
        }
        return calculateWeightedScore(
                review.getGameplayScore(),
                review.getGraphicsScore(),
                review.getSoundScore(),
                review.getStoryScore(),
                review.getReplayValueScore(),
                profile
        );
    }

    public static double calculateWeightedScore(Integer gameplay,
                                                Integer graphics,
                                                Integer sound,
                                                Integer story,
                                                Integer replayValue,
                                                RatingProfile profile) {
        if (profile == null) {
            profile = RatingProfile.DEFAULT;
        }

        Weights w = weightsFor(profile);

        // Core kryteria są wymagane niezależnie od profilu.
        assertScoreNotNull("gameplay", gameplay);
        assertScoreNotNull("graphics", graphics);
        assertScoreNotNull("sound", sound);
        assertScoreNotNull("replayValue", replayValue);

        // Story jest wymagane tylko w profilu NARRATIVE (gdy ma wagę > 0).
        if (w.story > 0 && profile == RatingProfile.NARRATIVE) {
            assertScoreNotNull("story", story);
        }

        double sumWeighted = 0.0;
        double sumWeights = 0.0;

        if (w.gameplay > 0) { sumWeighted += gameplay * w.gameplay; sumWeights += w.gameplay; }
        if (w.graphics > 0) { sumWeighted += graphics * w.graphics; sumWeights += w.graphics; }
        if (w.sound > 0) { sumWeighted += sound * w.sound; sumWeights += w.sound; }
        if (story != null && w.story > 0) { sumWeighted += story * w.story; sumWeights += w.story; }
        if (w.replayValue > 0) { sumWeighted += replayValue * w.replayValue; sumWeights += w.replayValue; }

        if (sumWeights <= 0.0) {
            throw new IllegalArgumentException("Brak kryteriów do obliczenia oceny (wszystkie N/A?)");
        }

        double normalized = sumWeighted / sumWeights;
        return round1(normalized);
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
                .map(r -> extractScoreNullable(r, criterion))
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        return round1(avg);
    }

    // ──────────────────────────────────────────────
    // Pomocnicze
    // ──────────────────────────────────────────────

    private static Integer extractScoreNullable(Review review, Criterion criterion) {
        if (review == null) return null;
        return switch (criterion) {
            case GAMEPLAY     -> review.getGameplayScore();
            case GRAPHICS     -> review.getGraphicsScore();
            case SOUND        -> review.getSoundScore();
            case STORY        -> review.getStoryScore();
            case REPLAY_VALUE -> review.getReplayValueScore();
        };
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static void assertScoreNotNull(String name, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Ocena '" + name + "' nie może być null");
        }
    }

    private record Weights(double gameplay, double graphics, double sound, double story, double replayValue) {}

    private static Weights weightsFor(RatingProfile profile) {
        return switch (profile) {
            case DEFAULT -> new Weights(
                    WEIGHT_GAMEPLAY, WEIGHT_GRAPHICS, WEIGHT_SOUND, WEIGHT_STORY, WEIGHT_REPLAY_VALUE
            );
            case NARRATIVE -> new Weights(
                    NARR_WEIGHT_GAMEPLAY, NARR_WEIGHT_GRAPHICS, NARR_WEIGHT_SOUND, NARR_WEIGHT_STORY, NARR_WEIGHT_REPLAY_VALUE
            );
            case MULTIPLAYER -> new Weights(
                    MP_WEIGHT_GAMEPLAY, MP_WEIGHT_GRAPHICS, MP_WEIGHT_SOUND, MP_WEIGHT_STORY, MP_WEIGHT_REPLAY_VALUE
            );
        };
    }

    /** Kryteria oceniania dostępne do użycia w {@link #calculateCriterionAverage}. */
    public enum Criterion {
        GAMEPLAY, GRAPHICS, SOUND, STORY, REPLAY_VALUE
    }
}
