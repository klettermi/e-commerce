package kr.hhplus.be.server.application.cart;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartRepository;
import kr.hhplus.be.server.interfaces.api.cart.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // 사용자 장바구니 조회 (없으면 새로 생성) → DTO 반환
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
        return CartDto.fromEntity(cart);
    }

    // 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트) → DTO 반환
    public CartDto addItem(Long userId, CartItem newItem) {
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
        return CartDto.fromEntity(cart);
    }

    // 장바구니 내 아이템 업데이트 (수량 수정)
    public CartDto updateItem(Long userId, CartItem updatedItem) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(updatedItem.getProductId())) {
                item.setQuantity(updatedItem.getQuantity());
                break;
            }
        }

        cart = cartRepository.save(cart);
        return CartDto.fromEntity(cart);
    }

    // 장바구니에서 아이템 제거 → DTO 반환
    public CartDto removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));
        cart = cartRepository.save(cart);
        return CartDto.fromEntity(cart);
    }

    // 장바구니 전체 비우기 → DTO 반환
    public CartDto clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.getCartItems().clear();
        cart = cartRepository.save(cart);
        return CartDto.fromEntity(cart);
    }
}
