package kr.hhplus.be.server.application.cart;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartOutput {
    private Long userId;
    private List<Item> items;

    @Getter
    @Builder
    public static class Item {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private int quantity;
    }
}