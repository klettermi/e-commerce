package kr.hhplus.be.server.interfaces.api.cart;


import jakarta.validation.Valid;
import kr.hhplus.be.server.application.cart.CartFacade;
import kr.hhplus.be.server.application.cart.CartOutput;
import kr.hhplus.be.server.application.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse.Cart> getCart(@PathVariable Long userId) {
        var req = CartRequest.Get.of(userId);
        CartOutput out = cartFacade.getCart(req.toInput());
        return ApiResponse.success(CartResponse.Cart.fromOutput(out));
    }

    @PostMapping("/{userId}/items")
    public ApiResponse<CartResponse.Cart> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartRequest.AddItem request
    ) {
        // pathVariable 과 body.userId 가 충돌하지 않도록, request.userId를 덮어씌워주는 것도 가능합니다:
        request = CartRequest.AddItem.of(userId, request.getProductId(), request.getQuantity());
        CartOutput out = cartFacade.addItem(request.toInput());
        return ApiResponse.success(CartResponse.Cart.fromOutput(out));
    }

    @PutMapping("/{userId}/items")
    public ApiResponse<CartResponse.Cart> updateItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartRequest.UpdateItem request
    ) {
        request = CartRequest.UpdateItem.of(userId, request.getProductId(), request.getQuantity());
        CartOutput out = cartFacade.updateItem(request.toInput());
        return ApiResponse.success(CartResponse.Cart.fromOutput(out));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<CartResponse.Cart> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        var req = CartRequest.RemoveItem.of(userId, productId);
        CartOutput out = cartFacade.removeItem(req.toInput());
        return ApiResponse.success(CartResponse.Cart.fromOutput(out));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<CartResponse.Cart> clearCart(@PathVariable Long userId) {
        var req = CartRequest.Clear.of(userId);
        CartOutput out = cartFacade.clearCart(req.toInput());
        return ApiResponse.success(CartResponse.Cart.fromOutput(out));
    }
}