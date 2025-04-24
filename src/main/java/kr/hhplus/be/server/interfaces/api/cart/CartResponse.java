package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.application.cart.CartOutput;
import kr.hhplus.be.server.domain.cart.Cart;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class CartResponse {
    private Long userId;
    private List<Item> items;

    public static CartResponse from(CartOutput output) {
        List<Item> items = output.getItems().stream()
                .map(i -> Item.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build())
                .toList();

        return CartResponse.builder()
                .userId(output.getUserId())
                .items(items)
                .build();
    }

    @Getter @Builder
    public static class Item {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}