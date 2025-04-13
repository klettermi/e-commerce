package kr.hhplus.be.server.domain.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        // 생성자를 통해 userId를 초기화합니다.
        cart = new Cart(1L);
    }

    @Test
    void addItemInCart_shouldAddItemAndSetCartReference() {
        CartItem item = CartItem.builder()
                .productId(1L)
                .productName("Test Product")
                .quantity(2)
                .price(BigDecimal.valueOf(10000))
                .build();

        // when: 장바구니에 아이템 추가
        cart.addItemInCart(item);

        // then: cartItems 리스트에 아이템이 추가되어야 하고, 해당 아이템의 연관관계(cart)가 올바르게 설정되어야 합니다.
        assertEquals(1, cart.getCartItems().size(), "아이템이 1건 추가되어야 합니다.");
        assertSame(cart, cart.getCartItems().get(0).getCart(), "CartItem의 cart 필드가 올바르게 설정되어야 합니다.");
    }

    @Test
    void removeItem_shouldRemoveItemAndUnsetCartReference() {
        CartItem item1 = CartItem.builder()
                .productId(1L)
                .productName("Test Product 1")
                .quantity(1)
                .price(BigDecimal.valueOf(10000))
                .build();

        CartItem item2 = CartItem.builder()
                .productId(2L)
                .productName("Test Product 2")
                .quantity(3)
                .price(BigDecimal.valueOf(10000))
                .build();

        // when: 두 아이템을 장바구니에 추가
        cart.addItemInCart(item1);
        cart.addItemInCart(item2);
        assertEquals(2, cart.getCartItems().size(), "총 2건의 아이템이 있어야 합니다.");

        // when: item1을 장바구니에서 제거
        cart.removeItem(item1);

        // then: cartItems 리스트에서 item1이 제거되고, 해당 아이템의 cart 레퍼런스가 null로 설정되어야 합니다.
        assertEquals(1, cart.getCartItems().size(), "아이템이 1건 남아있어야 합니다.");
        assertNull(item1.getCart(), "제거된 아이템의 cart는 null이어야 합니다.");
        // 남아있는 아이템은 item2임을 확인
        assertEquals(2, cart.getCartItems().get(0).getProductId(), "남은 아이템의 productId는 2여야 합니다.");
    }
}