package kr.hhplus.be.server.interfaces.api.product;

import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductInput;
import kr.hhplus.be.server.application.product.ProductOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/products")
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<ProductResponse.Page> lookupProducts(
            @RequestParam(defaultValue = "1") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "size는 1 이상이어야 합니다.") int size
    ) {
        ProductInput.List input = new ProductInput.List();
        input.setPage(page);
        input.setSize(size);

        ProductOutput.Page output = productFacade.getProductList(input);

        ProductResponse.Page response = ProductResponse.Page.fromOutput(output);

        return ApiResponse.success(response);
    }

    @GetMapping("/popular")
    public ApiResponse<ProductResponse.TopSellingList> getPopular(
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            int limit
    ) {
        ProductInput.TopSelling input = new ProductInput.TopSelling();
        input.setTopN(limit);

        ProductOutput.TopSellingList output = productFacade.getTopSellingProductsLast3Days(input);

        ProductResponse.TopSellingList response = ProductResponse.TopSellingList.fromOutput(output);

        return ApiResponse.success(response);
    }
}
