package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User dummyUser;
    private List<OrderProduct> reqs;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        // 공통 더미 데이터
        dummyUser = User.builder()
                .username("username")
                .build();

        orderNumber = "ORD-001";
        reqs = List.of(
                OrderProduct.builder()
                        .productId(10L)
                        .quantity(2)
                        .unitPoint(Money.of(500))
                        .build(),
                OrderProduct.builder()
                        .productId(20L)
                        .quantity(3)
                        .unitPoint(Money.of(200))
                        .build()
        );
    }

    @Test
    void placeOrder_successfulFlow() {
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.placeOrder(dummyUser, orderNumber, reqs);

        // 검증
        assertThat(result.getTotalPoint()).isEqualTo(Money.of(1600));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getOrderProducts()).hasSize(2)
                .extracting(OrderProduct::getProductId)
                .containsExactlyInAnyOrder(10L, 20L);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_insufficientStock_throwsException() {
        // when / then
        assertThatThrownBy(() ->
                orderService.placeOrder(dummyUser, orderNumber, reqs)
        )
                .isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("재고 부족: productId=10");

        verify(orderRepository, never()).save(any());
    }
}
