package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<Page<ProductResponse>> lookupProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productFacade.getProductList(pageable);
        Page<ProductResponse> responsePage = productPage.map(ProductResponse::fromEntity);
        return ApiResponse.success(responsePage);
    }

    @GetMapping("/popular")
    public ApiResponse<List<ProductResponse>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit
    ) {
        var products = productFacade.getTopSellingProductsLast3Days(limit);
        var dtoList = products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
        return ApiResponse.success(dtoList);
    }
}
