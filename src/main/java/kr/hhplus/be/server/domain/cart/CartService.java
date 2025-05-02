package kr.hhplus.be.server.domain.cart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    /**
     * 사용자 장바구니 조회 (없으면 새로 생성)
     */
    public CartInfo.Cart getCart(CartCommand.GetCart command) {
        Long userId = command.getUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()
                ));
        return toCartInfo(cart);
    }

    /**
     * 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트)
     */
    @Transactional
    public CartInfo.Cart addItem(CartCommand.AddItem command) {
        Long userId = command.getUserId();
        CartItem newItem = CartItem.builder()
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()
                ));

        boolean found = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) cart.addItemInCart(newItem);

        Cart updated = cartRepository.save(cart);
        return toCartInfo(updated);
    }

    /**
     * 장바구니 아이템 수량 업데이트
     */
    @Transactional
    public CartInfo.Cart updateItem(CartCommand.UpdateItem command) {
        Long userId = command.getUserId();
        CartItem updatedItem = CartItem.builder()
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()
                ));

        boolean found = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId().equals(updatedItem.getProductId())) {
                item.setQuantity(updatedItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) cart.addItemInCart(updatedItem);

        Cart updated = cartRepository.save(cart);
        return toCartInfo(updated);
    }

    /**
     * 장바구니에서 아이템 제거
     */
    @Transactional
    public CartInfo.Cart removeItem(CartCommand.RemoveItem command) {
        Long userId = command.getUserId();
        Long productId = command.getProductId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()
                ));

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));
        Cart updated = cartRepository.save(cart);
        return toCartInfo(updated);
    }

    /**
     * 장바구니 전체 비우기
     */
    @Transactional
    public CartInfo.Cart clearCart(CartCommand.ClearCart command) {
        Long userId = command.getUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()
                ));

        cart.getCartItems().clear();
        Cart updated = cartRepository.save(cart);
        return toCartInfo(updated);
    }

    private CartInfo.Cart toCartInfo(Cart cart) {
        return CartInfo.Cart.builder()
                .userId(cart.getUserId())
                .items(cart.getCartItems().stream()
                        .map(ci -> CartInfo.CartItem.builder()
                                .productId(ci.getProductId())
                                .productName(ci.getProductName())
                                .price(ci.getPrice().amount())
                                .quantity(ci.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}