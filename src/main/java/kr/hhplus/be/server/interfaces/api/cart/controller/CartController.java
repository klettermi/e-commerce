package kr.hhplus.be.server.interfaces.api.cart.controller;


import kr.hhplus.be.server.application.cart.CartService;
import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.interfaces.api.cart.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 사용자 장바구니 조회 (없으면 생성)
    @GetMapping("/{userId}")
    public ApiResponse<CartDto> getCart(@PathVariable Long userId) {
        CartDto cartDto = cartService.getCart(userId);
        return ApiResponse.success(cartDto);
    }

    // 장바구니에 아이템 추가 (동일 productId가 있으면 수량 업데이트)
    @PostMapping("/{userId}/items")
    public ApiResponse<CartDto> addItem(@PathVariable Long userId,
                                           @RequestBody CartItem newItem) {
        CartDto cartDto = cartService.addItem(userId, newItem);
        return ApiResponse.success(cartDto);
    }

    // 장바구니 내 아이템 업데이트 (수량 수정)
    @PutMapping("/{userId}/items")
    public ApiResponse<CartDto> updateItem(@PathVariable Long userId,
                                              @RequestBody CartItem updatedItem) {
        CartDto cartDto = cartService.updateItem(userId, updatedItem);
        return ApiResponse.success(cartDto);
    }

    // 장바구니에서 특정 아이템 제거
    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<CartDto> removeItem(@PathVariable Long userId,
                                              @PathVariable Long productId) {
        CartDto cartDto = cartService.removeItem(userId, productId);
        return ApiResponse.success(cartDto);
    }

    // 장바구니 전체 비우기
    @DeleteMapping("/{userId}")
    public ApiResponse<CartDto> clearCart(@PathVariable Long userId) {
        CartDto cartDto = cartService.clearCart(userId);
        return ApiResponse.success(cartDto);
    }
}
