package kr.hhplus.be.server.interfaces.api.cart;


import kr.hhplus.be.server.application.cart.CartService;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 사용자 장바구니 조회 (없으면 생성)
    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCart(userId);
        CartResponse cartResponse = CartResponse.from(cart);
        return ApiResponse.success(cartResponse);
    }

    // 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트)
    @PostMapping("/{userId}/items")
    public ApiResponse<CartResponse> addItem(@PathVariable Long userId,
                                             @RequestBody CartItemRequest newItem) {
        CartItem cartItem = newItem.toCartItem();
        Cart cart = cartService.addItem(userId, cartItem);
        CartResponse cartResponse = CartResponse.from(cart);
        return ApiResponse.success(cartResponse);
    }

    // 장바구니 내 아이템 업데이트 (수량 수정)
    @PutMapping("/{userId}/items")
    public ApiResponse<CartResponse> updateItem(@PathVariable Long userId,
                                                @RequestBody CartItemRequest updatedItem) {
        CartItem cartItem = updatedItem.toCartItem();
        Cart cart = cartService.updateItem(userId, cartItem);
        CartResponse cartResponse = CartResponse.from(cart);
        return ApiResponse.success(cartResponse);
    }

    // 장바구니에서 특정 아이템 제거
    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long userId,
                                                @PathVariable Long productId) {
        Cart cart = cartService.removeItem(userId, productId);
        CartResponse cartResponse = CartResponse.from(cart);

        return ApiResponse.success(cartResponse);
    }

    // 장바구니 전체 비우기
    @DeleteMapping("/{userId}")
    public ApiResponse<CartResponse> clearCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId);
        CartResponse cartResponse = CartResponse.from(cart);
        return ApiResponse.success(cartResponse);
    }
}
