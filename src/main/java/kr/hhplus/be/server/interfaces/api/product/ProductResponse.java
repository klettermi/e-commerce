package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.product.ProductOutput;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {

    @Getter @Builder
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

        public static Page fromOutput(ProductOutput.Page out) {
            List<Item> items = out.getProducts().stream()
                    .map(p -> Item.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .basePrice(p.getBasePrice())
                            .build())
                    .collect(Collectors.toList());

            return Page.builder()
                    .products(items)
                    .page(out.getPage())
                    .size(out.getSize())
                    .totalElements(out.getTotalElements())
                    .build();
        }
    }

    @Getter @Builder
    public static class TopSellingList {
        private List<Item> products;

        public static TopSellingList fromOutput(ProductOutput.TopSellingList out) {
            List<Item> items = out.getProducts().stream()
                    .map(p -> Item.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .basePrice(p.getBasePrice())
                            .build())
                    .collect(Collectors.toList());

            return TopSellingList.builder()
                    .products(items)
                    .build();
        }
    }
}
