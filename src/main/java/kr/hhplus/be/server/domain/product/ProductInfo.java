package kr.hhplus.be.server.domain.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class ProductInfo {

    @Getter
    @Builder
    public static class ProductDetail {
        private Long id;
        private String name;
        private BigDecimal basePrice;
    }

    @Getter
    @Builder
    public static class ProductPage {
        private List<ProductDetail> products;
        private int page;
        private int size;
        private long totalElements;
    }

    @Getter
    @Builder
    public static class TopSellingList {
        private List<ProductDetail> products;
    }
}
