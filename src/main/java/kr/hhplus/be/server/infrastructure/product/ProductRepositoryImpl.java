package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
}
