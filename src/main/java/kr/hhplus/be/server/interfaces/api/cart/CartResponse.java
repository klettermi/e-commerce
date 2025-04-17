package kr.hhplus.be.server.interfaces.api.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public record CartResponse(Long id, Long userId, List<CartItemRequest> cartItems) {

    @Transactional
    public static CartResponse fromEntity(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemRequest> items = cart.getCartItems().stream()
                .map(CartItemRequest::fromEntity)
                .collect(Collectors.toList());
        return new CartResponse(cart.getId(), cart.getUserId(), items);
    }
}