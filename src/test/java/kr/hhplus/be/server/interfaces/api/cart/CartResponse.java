package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public record CartResponse(Long id, Long userId, List<CartItemRequest> cartItems) {

    public static CartResponse from(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemRequest> items = cart.getCartItems().stream()
                .map(r -> CartItemRequest.builder()
                        .productId(r.getProductId())
                        .quantity(r.getQuantity())
                        .productName(r.getProductName())
                        .price(r.getPrice())
                        .build())
                .collect(Collectors.toList());
        return new CartResponse(cart.getId(), cart.getUserId(), items);
    }
}