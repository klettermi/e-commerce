package kr.hhplus.be.server.application.cart;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartRepository;
import kr.hhplus.be.server.interfaces.api.cart.CartItemRequest;
import kr.hhplus.be.server.interfaces.api.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // 사용자 장바구니 조회 (없으면 새로 생성) 
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
        return CartResponse.fromEntity(cart);
    }

    // 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트) 
    public CartResponse addItem(Long userId, CartItemRequest newItem) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        boolean found = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(newItem.productId())) {
                item.setQuantity(item.getQuantity() + newItem.quantity());
                found = true;
                break;
            }
        }
        if (!found) {
            cart.addItemInCart(CartItem.fromDto(newItem, cart));
        }

        cart = cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }

    public CartResponse updateItem(Long userId, CartItemRequest updatedItem) {
        // 사용자 장바구니 조회 (없으면 새로 생성)
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        boolean found = false;
        // 장바구니 아이템 업데이트
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(updatedItem.productId())) {
                item.setQuantity(updatedItem.quantity());
                found = true;
                break;
            }
        }
        if (!found) {
             cart.addItemInCart(CartItem.fromDto(updatedItem, cart));
        }

        cart = cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }


    // 장바구니에서 아이템 제거 
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));
        cart = cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }

    // 장바구니 전체 비우기 
    public CartResponse clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().clear();
        cart = cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }
}
