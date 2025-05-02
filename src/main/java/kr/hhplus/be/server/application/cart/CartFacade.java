package kr.hhplus.be.server.application.cart;

import kr.hhplus.be.server.domain.cart.CartCommand;
import kr.hhplus.be.server.domain.cart.CartInfo;
import kr.hhplus.be.server.domain.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;

    public CartOutput getCart(CartInput.Get input) {
        CartInfo.Cart info = cartService.getCart(
                CartCommand.GetCart.of(input.getUserId())
        );
        return toOutput(info);
    }

    public CartOutput addItem(CartInput.AddItem input) {
        CartInfo.Cart info = cartService.addItem(
                CartCommand.AddItem.of(
                        input.getUserId(),
                        input.getProductId(),
                        input.getQuantity()
                )
        );
        return toOutput(info);
    }

    public CartOutput updateItem(CartInput.UpdateItem input) {
        CartInfo.Cart info = cartService.updateItem(
                CartCommand.UpdateItem.of(
                        input.getUserId(),
                        input.getProductId(),
                        input.getQuantity()
                )
        );
        return toOutput(info);
    }

    public CartOutput removeItem(CartInput.RemoveItem input) {
        CartInfo.Cart info = cartService.removeItem(
                CartCommand.RemoveItem.of(
                        input.getUserId(),
                        input.getProductId()
                )
        );
        return toOutput(info);
    }

    public CartOutput clearCart(CartInput.Clear input) {
        CartInfo.Cart info = cartService.clearCart(
                CartCommand.ClearCart.of(input.getUserId())
        );
        return toOutput(info);
    }

    private CartOutput toOutput(CartInfo.Cart info) {
        return CartOutput.builder()
                .userId(info.getUserId())
                .items(info.getItems().stream()
                        .map(i -> CartOutput.Item.builder()
                                .productId(i.getProductId())
                                .productName(i.getProductName())
                                .price(i.getPrice())
                                .quantity(i.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}
