package kr.hhplus.be.server.domain.cart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response value objects for Cart operations
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartInfo {

    @Getter
    @Builder
    public static class Cart {
        private final Long userId;
        private final List<CartItem> items;
    }

    @Getter
    @Builder
    public static class CartItem {
        private final Long productId;
        private final String productName;
        private final BigDecimal price;
        private final int quantity;
    }
}
