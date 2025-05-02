package kr.hhplus.be.server.application.cart;

import kr.hhplus.be.server.domain.cart.CartCommand;
import kr.hhplus.be.server.domain.cart.CartInfo;
import kr.hhplus.be.server.domain.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartFacadeTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartFacade cartFacade;

    private final Long userId    = 7L;
    private final Long productId = 42L;
    private final int quantity   = 3;
    private final BigDecimal price = new BigDecimal("123.45");
    private CartInfo.Cart stubInfo;

    @BeforeEach
    void setUp() {
        // stub CartInfo.Cart 에서 getUserId, getItems() 리턴값 지정
        stubInfo = mock(CartInfo.Cart.class);
        when(stubInfo.getUserId()).thenReturn(userId);

        // 한 개의 CartItem 정보 stub
        CartInfo.CartItem infoItem = mock(CartInfo.CartItem.class);
        when(infoItem.getProductId()).thenReturn(productId);
        when(infoItem.getProductName()).thenReturn("TestProduct");
        when(infoItem.getQuantity()).thenReturn(quantity);
        when(infoItem.getPrice()).thenReturn(price);

        when(stubInfo.getItems()).thenReturn(List.of(infoItem));
    }

    @Test
    void getCart_delegatesAndMaps() {
        // given
        CartInput.Get input = new CartInput.Get();
        input.setUserId(userId);

        when(cartService.getCart(argThat(cmd ->
                cmd instanceof CartCommand.GetCart
                        && ((CartCommand.GetCart)cmd).getUserId().equals(userId)
        ))).thenReturn(stubInfo);

        // when
        CartOutput output = cartFacade.getCart(input);

        // then: 서비스 호출 검증
        verify(cartService).getCart(any(CartCommand.GetCart.class));

        // then: 매핑 검증
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getItems()).hasSize(1);

        CartOutput.Item outItem = output.getItems().get(0);
        assertThat(outItem.getProductId()).isEqualTo(productId);
        assertThat(outItem.getProductName()).isEqualTo("TestProduct");
        assertThat(outItem.getQuantity()).isEqualTo(quantity);
        assertThat(outItem.getPrice()).isEqualTo(price);
    }

    @Test
    void addItem_delegatesAndMaps() {
        // given
        CartInput.AddItem input = new CartInput.AddItem();
        input.setUserId(userId);
        input.setProductId(productId);
        input.setQuantity(quantity);

        when(cartService.addItem(argThat(cmd ->
                cmd instanceof CartCommand.AddItem
                        && ((CartCommand.AddItem)cmd).getUserId().equals(userId)
                        && ((CartCommand.AddItem)cmd).getProductId().equals(productId)
                        && ((CartCommand.AddItem)cmd).getQuantity() == quantity
        ))).thenReturn(stubInfo);

        // when
        CartOutput output = cartFacade.addItem(input);

        // then
        verify(cartService).addItem(any(CartCommand.AddItem.class));
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getItems()).hasSize(1);
    }

    @Test
    void updateItem_delegatesAndMaps() {
        // given
        CartInput.UpdateItem input = new CartInput.UpdateItem();
        input.setUserId(userId);
        input.setProductId(productId);
        input.setQuantity(quantity);

        when(cartService.updateItem(argThat(cmd ->
                cmd instanceof CartCommand.UpdateItem
                        && ((CartCommand.UpdateItem)cmd).getUserId().equals(userId)
                        && ((CartCommand.UpdateItem)cmd).getProductId().equals(productId)
                        && ((CartCommand.UpdateItem)cmd).getQuantity() == quantity
        ))).thenReturn(stubInfo);

        // when
        CartOutput output = cartFacade.updateItem(input);

        // then
        verify(cartService).updateItem(any(CartCommand.UpdateItem.class));
        assertThat(output.getItems()).hasSize(1);
    }

    @Test
    void removeItem_delegatesAndMaps() {
        // given
        CartInput.RemoveItem input = new CartInput.RemoveItem();
        input.setUserId(userId);
        input.setProductId(productId);

        when(cartService.removeItem(argThat(cmd ->
                cmd instanceof CartCommand.RemoveItem
                        && ((CartCommand.RemoveItem)cmd).getUserId().equals(userId)
                        && ((CartCommand.RemoveItem)cmd).getProductId().equals(productId)
        ))).thenReturn(stubInfo);

        // when
        CartOutput output = cartFacade.removeItem(input);

        // then
        verify(cartService).removeItem(any(CartCommand.RemoveItem.class));
        assertThat(output.getItems()).hasSize(1);
    }

    @Test
    void clearCart_delegatesAndMaps() {
        // given
        CartInput.Clear input = new CartInput.Clear();
        input.setUserId(userId);

        when(cartService.clearCart(argThat(cmd ->
                cmd instanceof CartCommand.ClearCart
                        && ((CartCommand.ClearCart)cmd).getUserId().equals(userId)
        ))).thenReturn(stubInfo);

        // when
        CartOutput output = cartFacade.clearCart(input);

        // then
        verify(cartService).clearCart(any(CartCommand.ClearCart.class));
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getItems()).hasSize(1);
    }
}
