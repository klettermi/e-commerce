package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;

@Builder
public record CartItemRequest(Long productId, String productName, int quantity, Money price) {


    public CartItem toCartItem() {
        return CartItem.builder()
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .build();
    }
}