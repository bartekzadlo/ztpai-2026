package pl.edu.pk.gamelibrary.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.model.Product;
import pl.edu.pk.gamelibrary.repository.ProductRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Zadania 2 & 3 – ProductService (Mockito)")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Nested
    @DisplayName("Zadanie 2 – getProductById")
    class GetProductById {

        @Test
        @DisplayName("zwraca produkt gdy findById(1L) go znajdzie")
        void getProductById_shouldReturnProduct_whenFound() {
            Product mockProduct = new Product("Laptop", "Wydajny laptop", 3999.99);
            when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

            Product result = productService.getProductById(1L);

            assertNotNull(result, "Zwrócony produkt nie powinien być null");
            assertEquals("Laptop", result.getName(),
                    "Nazwa produktu powinna być 'Laptop'");

            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy findById(99L) zwróci Optional.empty()")
        void getProductById_shouldThrow_whenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getProductById(99L),
                    "Powinien zostać rzucony ResourceNotFoundException gdy produkt nie istnieje");

            verify(productRepository, times(1)).findById(99L);
        }
    }


    @Nested
    @DisplayName("Zadanie 3 – createProduct")
    class CreateProduct {

        @Test
        @DisplayName("zapisuje produkt i zwraca go z nadanym id")
        void createProduct_shouldReturnSavedProductWithId() {
            Product input  = new Product("Klawiatura", "Mechaniczna", 249.0);
            Product saved  = new Product("Klawiatura", "Mechaniczna", 249.0);
            setId(saved, 42L);
            when(productRepository.save(any(Product.class))).thenReturn(saved);

            Product result = productService.createProduct(input);

            assertNotNull(result.getId(), "Zwrócony produkt powinien mieć nadane id");
            assertEquals(42L, result.getId(), "Id powinno wynosić 42");

            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy name jest null")
        void createProduct_shouldThrow_whenNameIsNull() {
            Product product = new Product(null, "opis", 100.0);

            assertThrows(IllegalArgumentException.class,
                    () -> productService.createProduct(product),
                    "Powinien zostać rzucony IllegalArgumentException gdy name == null");
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy name jest pustym stringiem")
        void createProduct_shouldThrow_whenNameIsBlank() {
            Product product = new Product("   ", "opis", 100.0);

            assertThrows(IllegalArgumentException.class,
                    () -> productService.createProduct(product),
                    "Powinien zostać rzucony IllegalArgumentException gdy name jest pusty");

            verify(productRepository, never()).save(any());
        }
    }

    private static void setId(Product product, Long id) {
        try {
            var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, id);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się ustawić id przez refleksję", e);
        }
    }
}
