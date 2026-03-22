package pl.edu.pk.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pk.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}