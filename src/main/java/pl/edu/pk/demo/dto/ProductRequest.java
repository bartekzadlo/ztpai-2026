package pl.edu.pk.demo.dto;

import jakarta.validation.constraints.*;

public class ProductRequest {

    @NotBlank(message = "Nazwa produktu nie może być pusta")
    @Size(min = 3, max = 100, message = "Nazwa musi mieć od 3 do 100 znaków")
    private String name;

    @NotNull(message = "Cena jest wymagana")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cena musi być większa lub równa 0")
    private Double price;

    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
