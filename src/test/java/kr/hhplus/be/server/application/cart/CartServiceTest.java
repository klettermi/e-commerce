package kr.hhplus.be.server.application.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartRepository;
import kr.hhplus.be.server.domain.common.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // getCart 테스트: 해당 userId에 대한 Cart가 없으면 새로 생성하여 저장
    @Test
    void testGetCart_whenCartDoesNotExist_thenCreateNewCart() {
        Long userId = 1L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        Cart newCart = new Cart(userId);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Cart cart = cartService.getCart(userId);

        assertNotNull(cart, "새로 생성된 Cart가 null이면 안 됩니다.");
        assertEquals(userId, cart.getUserId(), "userId가 동일해야 합니다.");
        verify(cartRepository).findByUserId(userId);
        verify(cartRepository).save(any(Cart.class));
    }

    // getCart 테스트: 해당 userId에 대한 Cart가 존재하면 그대로 반환
    @Test
    void testGetCart_whenCartExists_thenReturnExistingCart() {
        Long userId = 2L;
        Cart existingCart = new Cart(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));

        Cart cart = cartService.getCart(userId);

        assertNotNull(cart, "반환된 Cart가 null이면 안 됩니다.");
        assertEquals(userId, cart.getUserId(), "userId가 동일해야 합니다.");
        verify(cartRepository).findByUserId(userId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    // addItem 테스트: 동일 productId의 아이템이 없으면 추가
    @Test
    void testAddItem_whenItemNotExists_thenAddItem() {
        Long userId = 1L;
        Cart cart = new Cart(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartItem newItem = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(2)
                .price(Money.of(10000))
                .build();

        Cart cart1 = cartService.addItem(userId, newItem);

        assertEquals(1, cart1.getCartItems().size(), "아이템이 1건 추가되어야 합니다.");
        CartItem addedItem = cart1.getCartItems().get(0);
        assertEquals(1001L, addedItem.getProductId(), "상품 ID가 동일해야 합니다.");
        assertEquals("Product 1", addedItem.getProductName(), "상품명이 동일해야 합니다.");
        assertEquals(2, addedItem.getQuantity(), "수량이 2여야 합니다.");
        assertEquals(Money.of(10000), addedItem.getPrice(), "가격이 50.0이어야 합니다.");
        verify(cartRepository).save(cart);
    }

    // addItem 테스트: 동일 productId의 아이템이 이미 있으면 수량 업데이트
    @Test
    void testAddItem_whenItemAlreadyExists_thenUpdateQuantity() {
        Long userId = 1L;
        Cart cart = new Cart(userId);
        CartItem existingItem = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(2)
                .price(Money.of(10000))
                .build();
        cart.addItemInCart(existingItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // 새 아이템으로 수량 3을 추가 (기존 2 + 3 = 5가 되어야 함)
        CartItem newItem = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(3)
                .price(Money.of(10000))
                .build();

        Cart cart1 = cartService.addItem(userId, newItem);
        assertEquals(1, cart1.getCartItems().size(), "아이템은 1건이어야 합니다.");
        CartItem updatedItem = cart1.getCartItems().get(0);
        assertEquals(5, updatedItem.getQuantity(), "수량이 5여야 합니다.");
        verify(cartRepository).save(cart);
    }

    // updateItem 테스트: 해당 productId의 아이템 수량을 수정
    @Test
    void testUpdateItem() {
        Long userId = 1L;
        Cart cart = new Cart(userId);
        CartItem item = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(2)
                .price(Money.of(10000))
                .build();
        cart.addItemInCart(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // 수량을 5로 업데이트
        CartItem updatedItem = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(5)
                .price(Money.of(10000))
                .build();

        Cart cart1 = cartService.updateItem(userId, updatedItem);
        assertEquals(1, cart1.getCartItems().size(), "아이템은 1건이어야 합니다.");
        assertEquals(5, cart1.getCartItems().get(0).getQuantity(), "수량이 5여야 합니다.");
        verify(cartRepository).save(cart);
    }

    // removeItem 테스트: 지정한 productId의 아이템을 제거
    @Test
    void testRemoveItem() {
        Long userId = 1L;
        Cart cart = new Cart(userId);
        CartItem item1 = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(2)
                .price(Money.of(10000))
                .build();
        CartItem item2 = CartItem.builder()
                .productId(1002L)
                .productName("Product 2")
                .quantity(3)
                .price(Money.of(10000))
                .build();
        cart.addItemInCart(item1);
        cart.addItemInCart(item2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart cart1 = cartService.removeItem(userId, 1001L);
        assertEquals(1, cart1.getCartItems().size(), "제거 후 남은 아이템 수는 1건이어야 합니다.");
        assertEquals(1002L, cart1.getCartItems().get(0).getProductId(), "남은 아이템의 상품 ID는 1002여야 합니다.");
        verify(cartRepository).save(cart);
    }

    // clearCart 테스트: 장바구니의 모든 아이템을 제거
    @Test
    void testClearCart() {
        Long userId = 1L;
        Cart cart = new Cart(userId);
        CartItem item1 = CartItem.builder()
                .productId(1001L)
                .productName("Product 1")
                .quantity(2)
                .price(Money.of(10000))
                .build();
        CartItem item2 = CartItem.builder()
                .productId(1002L)
                .productName("Product 2")
                .quantity(3)
                .price(Money.of(10000))
                .build();
        cart.addItemInCart(item1);
        cart.addItemInCart(item2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart cart1 = cartService.clearCart(userId);
        assertTrue(cart1.getCartItems().isEmpty(), "장바구니가 비어 있어야 합니다.");
        verify(cartRepository).save(cart);
    }
}
