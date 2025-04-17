package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.interfaces.api.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public List<ProductResponse> getProductList() {
        return productService.getProductList();
    }
}
