package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.product.Product;
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

    private final ProductService productFacade;

    @GetMapping
    public ApiResponse<List<ProductResponse>> lookupProducts() {
        List<Product> userPoint = productFacade.getProductList();
        List<ProductResponse> productResponseList = userPoint.stream()
                .map(ProductResponse::from)
                .toList();
        return ApiResponse.success(productResponseList);
    }

    @GetMapping("/popular")
    public List<Map<String, Object>> getPopularProducts() {
        return List.of(
                Map.of("productId", 10, "name", "키보드", "totalSold", 100),
                Map.of("productId", 12, "name", "마우스", "totalSold", 90)
        );
    }
}
