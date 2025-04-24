package kr.hhplus.be.server.application.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartFacadeTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartFacade cartFacade;

    private final Long userId = 1L;
    private Cart cart;
    private CartItem item;

    @BeforeEach
    void setUp() {
        cart = new Cart(userId);
        item = CartItem.builder()
                .productId(100L)
                .quantity(2)
                .build();
    }

    @Test
    void getCart_delegatesToService() {
        when(cartService.getCart(userId)).thenReturn(cart);

        Cart result = cartFacade.getCart(userId);

        assertThat(result).isSameAs(cart);
        verify(cartService).getCart(userId);
    }

    @Test
    void addItem_delegatesToService() {
        when(cartService.addItem(userId, item)).thenReturn(cart);

        Cart result = cartFacade.addItem(userId, item);

        assertThat(result).isSameAs(cart);
        verify(cartService).addItem(userId, item);
    }

    @Test
    void updateItem_delegatesToService() {
        when(cartService.updateItem(userId, item)).thenReturn(cart);

        Cart result = cartFacade.updateItem(userId, item);

        assertThat(result).isSameAs(cart);
        verify(cartService).updateItem(userId, item);
    }

    @Test
    void removeItem_delegatesToService() {
        Long productId = 100L;
        when(cartService.removeItem(userId, productId)).thenReturn(cart);

        Cart result = cartFacade.removeItem(userId, productId);

        assertThat(result).isSameAs(cart);
        verify(cartService).removeItem(userId, productId);
    }

    @Test
    void clearCart_delegatesToService() {
        when(cartService.clearCart(userId)).thenReturn(cart);

        Cart result = cartFacade.clearCart(userId);

        assertThat(result).isSameAs(cart);
        verify(cartService).clearCart(userId);
    }
}
