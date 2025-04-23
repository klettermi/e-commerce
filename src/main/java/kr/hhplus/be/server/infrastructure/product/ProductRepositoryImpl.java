package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public long count() {
        return productJpaRepository.count();
    }

    @Override
    public void save(Product product) {
        productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Object[]> findTopProductSince(LocalDateTime startDate, Pageable pageable) {
        return orderJpaRepository.findTopProductSince(startDate, pageable);
    }
}
