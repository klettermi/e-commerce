package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.domain.product.dto.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<ProductResponse> getAll() {
        return List.of(
                new ProductResponse(1L, "기계식 키보드", 82000, 30),
                new ProductResponse(2L, "무선 마우스", 25000, 75)
        );
    }
}
