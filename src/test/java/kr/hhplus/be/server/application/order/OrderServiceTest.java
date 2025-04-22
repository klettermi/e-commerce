package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock InventoryRepository inventoryRepository;
    @InjectMocks OrderService orderService;

    private User dummyUser = User.builder().username("username").build();
    private String orderNumber = "ORD-001";
    private List<OrderProductRequest> reqs = List.of(
            new OrderProductRequest(10L,2,Money.of(500)),
            new OrderProductRequest(20L,3,Money.of(200))
    );

    @Test
    void placeOrder_successfulFlow() {
        // ─ 성공 시에만 호출되는 stub 들
        when(inventoryRepository.findByProductIdForUpdate(10L))
                .thenReturn(Optional.of(Inventory.builder().productId(10L).quantity(100).build()));
        when(inventoryRepository.findByProductIdForUpdate(20L))
                .thenReturn(Optional.of(Inventory.builder().productId(20L).quantity(100).build()));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.placeOrder(dummyUser, orderNumber, reqs);

        assertThat(result.getTotalPoint()).isEqualTo(Money.of(1600));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).save(any());
    }

    @Test
    void placeOrder_insufficientStock_throwsException() {
        // ─ 실패 시에만 호출되는 stub 만
        when(inventoryRepository.findByProductIdForUpdate(10L))
                .thenReturn(Optional.of(Inventory.builder().productId(10L).quantity(1).build()));

        assertThatThrownBy(() ->
                orderService.placeOrder(dummyUser, orderNumber, reqs)
        ).isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("재고 부족: productId=10");

        verify(orderRepository, never()).save(any());
    }
}

