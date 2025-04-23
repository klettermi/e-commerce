package kr.hhplus.be.server.domain.product;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();

    long count();

    void save(Product product);

    Optional<Product> findById(Long id);

    List<Object[]> findTopProductSince(LocalDateTime startDate, Pageable pageable);
}
