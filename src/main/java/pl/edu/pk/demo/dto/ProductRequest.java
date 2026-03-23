package pl.edu.pk.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotNull(message = "Nazwa wymagana")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "Cena wymagana")
    @Min(value = 0, message = "Cena >= 0")
    private Double price;

    @Size(max = 500)
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}