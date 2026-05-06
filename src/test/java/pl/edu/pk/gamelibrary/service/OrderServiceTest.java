package pl.edu.pk.gamelibrary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Zadanie 1 – OrderService")
class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    @DisplayName("calculateTotal zwraca 60.0 dla listy [10.0, 20.0, 30.0]")
    void calculateTotal_shouldReturnCorrectSum() {
        List<Double> prices = List.of(10.0, 20.0, 30.0);

        double total = orderService.calculateTotal(prices);

        assertEquals(60.0, total, 0.001,
                "Suma cen [10.0, 20.0, 30.0] powinna wynosić 60.0");
    }

    @Test
    @DisplayName("calculateTotal zwraca 0.0 dla pustej listy")
    void calculateTotal_shouldReturnZeroForEmptyList() {
        List<Double> prices = Collections.emptyList();

        double total = orderService.calculateTotal(prices);

        assertEquals(0.0, total, 0.001,
                "Suma cen dla pustej listy powinna wynosić 0.0");
    }

    @Test
    @DisplayName("calculateTotal rzuca IllegalArgumentException gdy argument to null")
    void calculateTotal_shouldThrowWhenPricesIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.calculateTotal(null),
                "Powinien zostać rzucony IllegalArgumentException dla null");
    }
}
