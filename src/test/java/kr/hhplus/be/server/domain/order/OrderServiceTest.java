package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderProduct p1, p2;
    private Money totalPoint;

    @BeforeEach
    void setUp() {
        p1 = OrderProduct.builder()
                .productId(1L)
                .quantity(2)
                .unitPoint(new Money(BigDecimal.valueOf(500)))
                .build();

        p2 = OrderProduct.builder()
                .productId(2L)
                .quantity(3)
                .unitPoint(new Money(BigDecimal.valueOf(200)))
                .build();

        // 2*500 + 3*200 = 1600
        totalPoint = new Money(BigDecimal.valueOf(1600));
    }

    @Test
    void calculateTotal_withMultipleItems_returnsSum() {
        List<OrderProduct> items = List.of(p1, p2);

        Money result = orderService.calculateTotal(items);

        assertEquals(totalPoint, result);
    }

    @Test
    void calculateTotal_withEmptyList_returnsZero() {
        Money result = orderService.calculateTotal(Collections.emptyList());

        assertEquals(Money.ZERO, result);
    }

    @Test
    void buildOrder_createsOrderWithCorrectFields() {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(7L);

        Order order = orderService.buildOrder(mockUser, List.of(p1, p2), totalPoint);

        assertEquals(7L, order.getUserId());
        assertEquals(totalPoint, order.getTotalPoint());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(2, order.getOrderProducts().size());

        OrderProduct added = order.getOrderProducts().get(0);
        assertEquals(p1.getProductId(), added.getProductId());
        assertEquals(p1.getQuantity(), added.getQuantity());
        assertEquals(p1.getUnitPoint(), added.getUnitPoint());
    }

    @Test
    void saveOrder_delegatesToRepository_andReturnsSavedOrder() {
        Order dummy = new Order(7L, totalPoint, OrderStatus.CREATED);
        when(orderRepository.save(dummy)).thenReturn(dummy);

        Order saved = orderService.saveOrder(dummy);

        assertSame(dummy, saved);
        verify(orderRepository).save(dummy);
    }

    @Test
    void getOrderById_whenFound_returnsOrder() {
        Order dummy = new Order(9L, totalPoint, OrderStatus.CREATED);
        when(orderRepository.findById(99L)).thenReturn(Optional.of(dummy));

        Order found = orderService.getOrderById(99L);

        assertSame(dummy, found);
        verify(orderRepository).findById(99L);
    }

    @Test
    void getOrderById_whenNotFound_throwsEntityNotFoundException() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getOrderById(100L)
        );
        assertTrue(ex.getMessage().contains("Order not found with id: 100"));
        verify(orderRepository).findById(100L);
    }
}
