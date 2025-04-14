package kr.hhplus.be.server.interfaces.api.cart.dto;

import kr.hhplus.be.server.domain.cart.Cart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public record CartDto(Long id, Long userId, List<CartItemDto> cartItems) {

    @Transactional
    public static CartDto fromEntity(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemDto> items = cart.getCartItems().stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());
        return new CartDto(cart.getId(), cart.getUserId(), items);
    }
}