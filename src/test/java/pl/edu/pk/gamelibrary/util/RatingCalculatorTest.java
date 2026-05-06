package pl.edu.pk.gamelibrary.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RatingCalculator – obliczanie średniej oceny gry")
class RatingCalculatorTest {

    private RatingCalculator ratingCalculator;

    @BeforeEach
    void setUp() {
        ratingCalculator = new RatingCalculator();
    }

    @Test
    @DisplayName("calculateAverage zwraca 4.0 dla ocen [3, 4, 5]")
    void calculateAverage_shouldReturnCorrectAverage() {
        List<Integer> ratings = List.of(3, 4, 5);

        double average = ratingCalculator.calculateAverage(ratings);

        assertEquals(4.0, average, 0.001,
                "Średnia ocen [3, 4, 5] powinna wynosić 4.0");
    }

    @Test
    @DisplayName("calculateAverage zwraca 0.0 dla pustej listy")
    void calculateAverage_shouldReturnZeroForEmptyList() {
        List<Integer> ratings = Collections.emptyList();

        double average = ratingCalculator.calculateAverage(ratings);

        assertEquals(0.0, average, 0.001,
                "Średnia dla pustej listy powinna wynosić 0.0");
    }

    @Test
    @DisplayName("calculateAverage rzuca IllegalArgumentException gdy argument to null")
    void calculateAverage_shouldThrowWhenRatingsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ratingCalculator.calculateAverage(null),
                "Powinien zostać rzucony IllegalArgumentException dla null");
    }
}
