package kr.hhplus.be.server.application.cart;


import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;

    public Cart getCart(Long userId) {
        return cartService.getCart(userId);
    }

    public Cart addItem(Long userId, CartItem newItem) {
        return cartService.addItem(userId, newItem);
    }

    public Cart updateItem(Long userId, CartItem updatedItem) {
        return cartService.updateItem(userId, updatedItem);
    }

    public Cart removeItem(Long userId, Long productId) {
        return cartService.removeItem(userId, productId);
    }

    public Cart clearCart(Long userId) {
        return cartService.clearCart(userId);
    }
}
