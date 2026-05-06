package pl.edu.pk.gamelibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pk.gamelibrary.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}