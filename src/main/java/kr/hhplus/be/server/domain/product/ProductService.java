package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * 전체 상품 페이징 조회 (커맨드 → Info)
     */
    public ProductInfo.ProductPage getProductList(ProductCommand.GetProductList command) {
        var pageable = command.getPageable();
        Page<Product> page = productRepository.findAll(pageable);

        List<ProductInfo.ProductDetail> details = page.getContent().stream()
                .map(p -> ProductInfo.ProductDetail.builder()
                        .id(p.getId())
                        .name(p.getItem().getName())
                        .basePrice(p.getItem().getBasePrice().amount())
                        .build()
                )
                .collect(Collectors.toList());

        return ProductInfo.ProductPage.builder()
                .products(details)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .build();
    }

    /**
     * 최근 3일간 판매량 상위 n개 조회 (커맨드 → Info)
     */
    public ProductInfo.TopSellingList getTopSellingProductsLast3Days(ProductCommand.GetTopSelling command) {
        LocalDateTime since = LocalDateTime.now().minusDays(3);
        var pageReq = PageRequest.of(0, command.getTopN());

        List<ProductInfo.ProductDetail> details = productRepository
                .findTopProductSince(since, pageReq).stream()
                .map(row -> (Long) row[0])
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + id))
                )
                .map(p -> ProductInfo.ProductDetail.builder()
                        .id(p.getId())
                        .name(p.getItem().getName())
                        .basePrice(p.getItem().getBasePrice().amount())
                        .build()
                )
                .collect(Collectors.toList());

        return ProductInfo.TopSellingList.builder()
                .products(details)
                .build();
    }

}
