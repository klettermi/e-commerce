package kr.hhplus.be.server.interfaces.api.cart.dto;

import kr.hhplus.be.server.domain.cart.CartItem;
import java.math.BigDecimal;

public record CartItemDto(Long id, Long productId, String productName, int quantity, BigDecimal price) {

    public static CartItemDto fromEntity(CartItem item) {
        if (item == null) {
            return null;
        }
        return new CartItemDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice());
    }
}