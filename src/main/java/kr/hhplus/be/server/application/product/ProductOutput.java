package kr.hhplus.be.server.application.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class ProductOutput {

    @Getter
    @Builder
    public static class Item {
        private Long id;
        private String name;
        private BigDecimal basePrice;
    }

    @Getter
    @Builder
    public static class Page {
        private List<Item> products;
        private int page;
        private int size;
        private long totalElements;
    }

    @Getter
    @Builder
    public static class TopSellingList {
        private List<Item> products;
    }
}