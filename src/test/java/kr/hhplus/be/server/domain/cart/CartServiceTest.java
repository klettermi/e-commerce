package kr.hhplus.be.server.domain.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private final Long USER_ID = 1L;

    private Cart emptyCart;
    private Cart existingCart;

    @BeforeEach
    void setUp() {
        // 빈 카트 세팅
        emptyCart = new Cart(USER_ID);
        // 아이템 하나 들어있는 카트
        existingCart = new Cart(USER_ID);
        existingCart.addItemInCart(CartItem.builder()
                        .productId(100L)
                        .quantity(2)
                         .build());
    }

    @Test
    void getCart_whenCartExists_shouldReturnIt() {
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));

        Cart result = cartService.getCart(USER_ID);

        assertSame(existingCart, result);
        verify(cartRepository, times(1)).findByUserId(USER_ID);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCart_whenCartNotExists_shouldCreateAndReturnNew() {
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.getCart(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertTrue(result.getCartItems().isEmpty());
        verify(cartRepository).findByUserId(USER_ID);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_whenNewProduct_shouldAddToCart() {
        CartItem newItem = CartItem.builder()
                .productId(200L)
                .quantity(3)
                .build();
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.addItem(USER_ID, newItem);

        assertEquals(1, result.getCartItems().size());
        assertEquals(newItem.getProductId(), result.getCartItems().get(0).getProductId());
        assertEquals(3, result.getCartItems().get(0).getQuantity());
        verify(cartRepository).save(emptyCart);
    }

    @Test
    void addItem_whenExistingProduct_shouldIncreaseQuantity() {
        CartItem newItem = CartItem.builder()
                .productId(100L)
                .quantity(5)
                .build();
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.addItem(USER_ID, newItem);

        assertEquals(1, result.getCartItems().size());
        assertEquals(100L, result.getCartItems().get(0).getProductId());
        assertEquals(7, result.getCartItems().get(0).getQuantity()); // 기존 2 + 5
        verify(cartRepository).save(existingCart);
    }

    @Test
    void updateItem_whenExistingProduct_shouldSetNewQuantity() {
        CartItem updated = CartItem.builder()
                .productId(100L)
                .quantity(10)
                .build();
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.updateItem(USER_ID, updated);

        assertEquals(1, result.getCartItems().size());
        assertEquals(10, result.getCartItems().get(0).getQuantity());
        verify(cartRepository).save(existingCart);
    }

    @Test
    void updateItem_whenNewProduct_shouldAddItem() {
        CartItem updated = CartItem.builder()
                .productId(300L)
                .quantity(4)
                .build();
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.updateItem(USER_ID, updated);

        assertEquals(2, result.getCartItems().size());
        // 두 번째로 추가된 아이템 확인
        Optional<CartItem> added = result.getCartItems().stream()
                .filter(i -> i.getProductId().equals(300L)).findFirst();
        assertTrue(added.isPresent());
        assertEquals(4, added.get().getQuantity());
        verify(cartRepository).save(existingCart);
    }

    @Test
    void removeItem_shouldRemoveMatchingProduct() {
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.removeItem(USER_ID, 100L);

        assertTrue(result.getCartItems().isEmpty());
        verify(cartRepository).save(existingCart);
    }

    @Test
    void clearCart_shouldRemoveAllItems() {
        when(cartRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.clearCart(USER_ID);

        assertTrue(result.getCartItems().isEmpty());
        verify(cartRepository).save(existingCart);
    }
}
