package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.application.cart.CartOutput;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    @Getter @Builder
    public static class Cart {
        private Long userId;
        private List<Item> items;

        public static Cart fromOutput(CartOutput out) {
            var items = out.getItems().stream()
                    .map(i -> Item.builder()
                            .productId(i.getProductId())
                            .productName(i.getProductName())
                            .quantity(i.getQuantity())
                            .price(i.getPrice())
                            .build())
                    .toList();

            return Cart.builder()
                    .userId(out.getUserId())
                    .items(items)
                    .build();
        }
    }

    @Getter @Builder
    public static class Item {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}