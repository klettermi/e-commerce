package kr.hhplus.be.server.interfaces.api.cart.dto;

import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemDto( Long productId, String productName, int quantity, Money price) {

    public static CartItemDto fromEntity(CartItem item) {
        if (item == null) {
            return null;
        }
        return new CartItemDto(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice());
    }
}