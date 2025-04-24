package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * 최근 3일간 판매량이 가장 높은 상품 상위 n개를 조회합니다.
     */
    public List<Product> getTopSellingProductsLast3Days(int topN) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        var page = PageRequest.of(0, topN);
        var rows = productRepository.findTopProductSince(threeDaysAgo, page);

        if (rows.isEmpty()) {
            return List.of();
        }

        return rows.stream()
                .map(row -> (Long) row[0])
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + id)))
                .toList();
    }
}
