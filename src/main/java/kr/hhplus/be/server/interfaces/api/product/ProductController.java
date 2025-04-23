package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> lookupProducts() {
        List<Product> productList = productService.getProductList();
        List<ProductResponse> productResponseList = productList.stream()
                .map(ProductResponse::fromEntity)
                .toList();
        return ApiResponse.success(productResponseList);
    }

    @GetMapping("/top-selling-products")
    public ApiResponse<List<ProductResponse>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit
    ) {
        var products = productService.getTopSellingProductsLast3Days(limit);
        var dtoList = products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
        return ApiResponse.success(dtoList);
    }
}
