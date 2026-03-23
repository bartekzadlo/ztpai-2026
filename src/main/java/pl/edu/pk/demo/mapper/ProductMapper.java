package pl.edu.pk.demo.mapper;

import pl.edu.pk.demo.dto.ProductRequest;
import pl.edu.pk.demo.dto.ProductResponse;
import pl.edu.pk.demo.model.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequest req) {
        Product p = new Product();
        p.setName(req.getName());
        p.setPrice(req.getPrice());
        p.setDescription(req.getDescription());
        return p;
    }

    public static ProductResponse toResponse(Product p) {
        ProductResponse res = new ProductResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setPrice(p.getPrice());
        return res;
    }
}