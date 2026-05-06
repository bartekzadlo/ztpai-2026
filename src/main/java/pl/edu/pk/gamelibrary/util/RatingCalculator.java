package pl.edu.pk.gamelibrary.util;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Narzędzie do obliczania średniej oceny gry na podstawie listy recenzji.
 * Używane przez GameService przy agregacji ocen (etap 4.0+).
 */
@Component
public class RatingCalculator {

    /**
     * Oblicza średnią ocenę z listy.
     *
     * @param ratings lista ocen (nie może być null, oceny w zakresie 1-5)
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
}
