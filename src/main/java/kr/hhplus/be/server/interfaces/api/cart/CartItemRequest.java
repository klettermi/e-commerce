package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;

@Builder
public record CartItemRequest(Long productId, String productName, int quantity, Money price) {

    public static CartItemRequest fromEntity(CartItem item) {
        if (item == null) {
            return null;
        }
        return new CartItemRequest(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice());
    }

    public static CartItem toEntity(CartItemRequest request) {
        return CartItem.builder()
                .productId(request.productId())
                .productName(request.productName())
                .quantity(request.quantity())
                .price(request.price())
                .build();
    }
}