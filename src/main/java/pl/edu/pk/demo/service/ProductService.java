package pl.edu.pk.demo.service;

import org.springframework.stereotype.Service;
import pl.edu.pk.demo.exception.ResourceNotFoundException;
import pl.edu.pk.demo.model.Product;
import pl.edu.pk.demo.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Zwraca produkt lub rzuca ResourceNotFoundException (-> 404).
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt", id));
    }

    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Nazwa produktu nie może być null ani pusta");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // Najpierw sprawdź istnienie – rzuci 404 jeśli brak
        getProductById(id);
        productRepository.deleteById(id);
    }
}
