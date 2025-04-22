package kr.hhplus.be.server.application.cart;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // 사용자 장바구니 조회 (없으면 새로 생성) 
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
    }

    // 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트)
    @Transactional
    public Cart addItem(Long userId, CartItem newItem) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        boolean found = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            cart.addItemInCart(newItem);
        }

        cart = cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart updateItem(Long userId, CartItem updatedItem) {
        // 사용자 장바구니 조회 (없으면 새로 생성)
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        boolean found = false;
        // 장바구니 아이템 업데이트
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(updatedItem.getProductId())) {
                item.setQuantity(updatedItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
             cart.addItemInCart(updatedItem);
        }

        cart = cartRepository.save(cart);
        return cart;
    }


    // 장바구니에서 아이템 제거
    @Transactional
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));
        cart = cartRepository.save(cart);
        return cart;
    }

    // 장바구니 전체 비우기
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().clear();
        cart = cartRepository.save(cart);
        return cart;
    }
}
