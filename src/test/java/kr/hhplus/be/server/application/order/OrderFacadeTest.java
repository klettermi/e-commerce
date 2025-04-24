package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.inventory.InventoryService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderFacade orderFacade;

    private final Long userId = 7L;
    private List<OrderProduct> products;
    private Money totalPoints;
    private User user;
    private Order builtOrder;

    @BeforeEach
    void setUp() {
        // 공통 더미 데이터
        products = List.of(
                OrderProduct.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPoint(Money.of(500))
                        .build(),
                OrderProduct.builder()
                        .productId(2L)
                        .quantity(3)
                        .unitPoint(Money.of(200))
                        .build()
        );
        totalPoints = Money.of(2*500 + 3*200); // 1600
        user = User.builder().id(userId).username("tester").build();
        builtOrder = Order.builder()
                .userId(userId)
                .orderNumber("ORD-123")
                .totalPoint(totalPoints)
                .status(OrderStatus.PAID)
                .orderProducts(products)
                .build();
    }

    @Test
    void getOrder_delegatesToOrderService() {
        Long orderId = 99L;
        Order expected = new Order();
        when(orderService.getOrderById(orderId)).thenReturn(expected);

        Order result = orderFacade.getOrder(orderId);

        assertThat(result).isSameAs(expected);
        verify(orderService).getOrderById(orderId);
        verifyNoInteractions(userService, inventoryService);
    }

    @Test
    void placeOrder_successfulFlow() {
        // stub user lookup
        when(userService.getUser(userId)).thenReturn(user);
        // stub calculateTotal & buildOrder & saveOrder
        when(orderService.calculateTotal(products)).thenReturn(totalPoints);
        when(orderService.buildOrder(user, products, totalPoints)).thenReturn(builtOrder);
        when(orderService.saveOrder(builtOrder)).thenReturn(builtOrder);
        // inventory check passes
        doNothing().when(inventoryService).checkAndDecreaseStock(products);

        Order result = orderFacade.placeOrder(userId, products);

        // verify interactions
        verify(userService).getUser(userId);
        verify(orderService).calculateTotal(products);
        verify(orderService).buildOrder(user, products, totalPoints);
        verify(inventoryService).checkAndDecreaseStock(products);
        verify(orderService).saveOrder(builtOrder);

        // assertions
        assertThat(result).isSameAs(builtOrder);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getTotalPoint()).isEqualTo(totalPoints);
        assertThat(result.getOrderProducts()).containsExactlyElementsOf(products);
    }

    @Test
    void placeOrder_whenUserNotFound_throwsEntityNotFound() {
        when(userService.getUser(userId))
                .thenThrow(new IllegalArgumentException("User not found"));

        assertThatThrownBy(() -> orderFacade.placeOrder(userId, products))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userService).getUser(userId);
        verifyNoMoreInteractions(orderService, inventoryService);
    }

    @Test
    void placeOrder_whenInventoryFails_throwsInvalidState() {
        when(userService.getUser(userId)).thenReturn(user);
        when(orderService.calculateTotal(products)).thenReturn(totalPoints);
        when(orderService.buildOrder(user, products, totalPoints)).thenReturn(builtOrder);
        doThrow(new InvalidStateException("재고 부족"))
                .when(inventoryService).checkAndDecreaseStock(products);

        assertThatThrownBy(() -> orderFacade.placeOrder(userId, products))
                .isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("재고 부족");

        verify(userService).getUser(userId);
        verify(orderService).calculateTotal(products);
        verify(orderService).buildOrder(user, products, totalPoints);
        verify(inventoryService).checkAndDecreaseStock(products);
        verify(orderService, never()).saveOrder(any());
    }
}
