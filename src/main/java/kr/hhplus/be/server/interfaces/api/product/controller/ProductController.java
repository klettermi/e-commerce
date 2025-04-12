package kr.hhplus.be.server.interfaces.api.product.controller;

import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<List<ProductDto>> lookupProducts() {
        return ApiResponse.success(productFacade.getProductList());
    }

    @GetMapping("/popular")
    public List<Map<String, Object>> getPopularProducts() {
        return List.of(
                Map.of("productId", 10, "name", "키보드", "totalSold", 100),
                Map.of("productId", 12, "name", "마우스", "totalSold", 90)
        );
    }
}
