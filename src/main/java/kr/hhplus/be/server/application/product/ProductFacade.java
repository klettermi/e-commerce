package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    /**
     * 페이징된 상품 목록 조회
     */
    public ProductOutput.Page getProductList(ProductInput.List input) {
        var pageable = PageRequest.of(input.getPage(), input.getSize());
        ProductInfo.ProductPage info = productService.getProductList(
                ProductCommand.GetProductList.of(pageable)
        );
        var items = info.getProducts().stream()
                .map(p -> ProductOutput.Item.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .basePrice(p.getBasePrice())
                        .build())
                .collect(Collectors.toList());

        return ProductOutput.Page.builder()
                .products(items)
                .page(info.getPage())
                .size(info.getSize())
                .totalElements(info.getTotalElements())
                .build();
    }

    /**
     * 최근 3일간 최다 판매 상품 조회 (Top N) - 캐시 적용
     */
    @Cacheable(value = "topSellingLast3Days", key = "#input.topN")
    public ProductOutput.TopSellingList getTopSellingProductsLast3Days(ProductInput.TopSelling input) {
        ProductInfo.TopSellingList info = productService.getTopSellingProductsLast3Days(
                ProductCommand.GetTopSelling.of(input.getTopN())
        );
        var items = info.getProducts().stream()
                .map(p -> ProductOutput.Item.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .basePrice(p.getBasePrice())
                        .build())
                .collect(Collectors.toList());

        return ProductOutput.TopSellingList.builder()
                .products(items)
                .build();
    }

    /**
     * 최근 3일간 최다 판매 상품 캐시 무효화
     */
    @CacheEvict(value = "topSellingLast3Days", allEntries = true)
    public void evictTopSellingLast3DaysCache() {
        // 빈 메서드로 캐시 무효화 처리
    }
}
