package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    public Page<Product> getProductList(Pageable pageable) {
        return productService.getProductList(pageable);
    }

    public List<Product> getTopSellingProductsLast3Days(int topN) {
        return productService.getTopSellingProductsLast3Days(topN);
    }
}
