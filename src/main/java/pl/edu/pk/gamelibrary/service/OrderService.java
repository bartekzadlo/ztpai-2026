package pl.edu.pk.gamelibrary.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    /**
     * Oblicza sumę cen z listy.
     *
     * @param prices lista cen (nie może być null)
     * @return suma cen, 0.0 dla pustej listy
     * @throws IllegalArgumentException gdy prices == null
     */
    public double calculateTotal(List<Double> prices) {
        if (prices == null) {
            throw new IllegalArgumentException("Lista cen nie może być null");
        }
        return prices.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
