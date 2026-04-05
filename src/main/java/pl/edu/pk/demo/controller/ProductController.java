package pl.edu.pk.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.demo.dto.ProductRequest;
import pl.edu.pk.demo.dto.ProductResponse;
import pl.edu.pk.demo.mapper.ProductMapper;
import pl.edu.pk.demo.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** GET /api/products -> 200 z listą */
    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAllProducts().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    /** GET /api/products/{id} -> 200 lub 404 (obsługa w GlobalExceptionHandler) */
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return ProductMapper.toResponse(service.getProductById(id));
    }

    /** POST /api/products -> 201 lub 400 przy złych danych */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = ProductMapper.toResponse(
                service.createProduct(ProductMapper.toEntity(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** DELETE /api/products/{id} -> 204 lub 404 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
