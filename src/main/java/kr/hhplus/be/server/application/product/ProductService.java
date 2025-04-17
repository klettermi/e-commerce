package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.interfaces.api.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductList() {
        List<Product> products = productJpaRepository.findAll();
        return products.stream()
                .map(Product::toDto)
                .collect(Collectors.toList());
    }
}
