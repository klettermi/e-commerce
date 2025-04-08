package kr.hhplus.be.server.api.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Map<String, Object>> getProducts() {
        return List.of(
                Map.of("id", 10, "name", "키보드", "price", 50000, "stock", 20),
                Map.of("id", 12, "name", "마우스", "price", 30000, "stock", 50)
        );
    }

    @GetMapping("/popular")
    public List<Map<String, Object>> getPopularProducts() {
        return List.of(
                Map.of("productId", 10, "name", "키보드", "totalSold", 100),
                Map.of("productId", 12, "name", "마우스", "totalSold", 90)
        );
    }
}
