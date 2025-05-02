package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.InventoryCommand;
import kr.hhplus.be.server.domain.inventory.InventoryService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock private OrderService orderService;
    @Mock private UserService userService;
    @Mock private InventoryService inventoryService;
    @InjectMocks private OrderFacade orderFacade;

    @Test
    void getOrder_delegatesAndMaps() {
        // given
        long orderId = 42L, userId = 7L;
        Money total = Money.of(1234);

        // 도메인 OrderInfo.OrderDetail을 실제 빌더로 생성
        OrderInfo.OrderProductInfo pinfo = OrderInfo.OrderProductInfo.builder()
                .productId(100L)
                .quantity(2)
                .unitPoint(Money.of(500))
                .build();
        OrderInfo.OrderDetail detail = OrderInfo.OrderDetail.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPoint(total)
                .status("PAID")
                .items(List.of(pinfo))
                .build();

        // stub
        when(orderService.getOrderById(any(OrderCommand.GetOrder.class)))
                .thenReturn(detail);

        // when
        OrderOutput out = orderFacade.getOrder(new OrderInput.Get(orderId));

        // then: service 호출 검증
        verify(orderService).getOrderById(argThat(cmd -> cmd.getOrderId().equals(orderId)));
        verifyNoInteractions(userService, inventoryService);

        // then: 매핑 검증
        assertThat(out.getOrderId()).isEqualTo(orderId);
        assertThat(out.getUserId()).isEqualTo(userId);
        assertThat(out.getTotalPoint()).isEqualTo(total);
        assertThat(out.getStatus()).isEqualTo("PAID");
        assertThat(out.getItems()).hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.getProductId()).isEqualTo(100L);
                    assertThat(item.getQuantity()).isEqualTo(2);
                    assertThat(item.getUnitPoint()).isEqualTo(Money.of(500));
                });
    }

    @Test
    void placeOrder_fullFlow_delegatesAndMaps() {
        // given
        final long userId = 7L;
        List<OrderInput.Item> dtoItems = List.of(
                new OrderInput.Item(1L, Money.of(10000), 2),
                new OrderInput.Item(2L, Money.of(20000), 3)
        );
        OrderInput.Place input = new OrderInput.Place(userId, dtoItems);

        // 1) 사용자 검증
        UserInfo.UserDetail dummyUserDetail = UserInfo.UserDetail.builder()
                .id(userId)
                .username("tester")
                .build();
        when(userService.getUser(argThat(cmd -> cmd.getUserId().equals(userId))))
                .thenReturn(dummyUserDetail);

        // 2) 재고 차감 (void 메서드)
        doNothing().when(inventoryService)
                .checkAndDecreaseStock(any(InventoryCommand.DecreaseStock.class));

        // 3) 총 포인트 계산
        Money expectedTotal = Money.of(2*500 + 3*200);
        OrderInfo.Total totalInfo = OrderInfo.Total.builder()
                .totalPoint(expectedTotal)
                .build();
        when(orderService.calculateTotal(argThat(cmd ->
                cmd.getItems().size() == 2
        ))).thenReturn(totalInfo);

        // 4) 주문 엔티티 생성
        Order fakeOrder = mock(Order.class);
        when(orderService.createOrderEntity(any(OrderCommand.BuildOrder.class)))
                .thenReturn(fakeOrder);

        // 5) 저장 및 detail 반환
        OrderInfo.OrderProductInfo savedPinfo = OrderInfo.OrderProductInfo.builder()
                .productId(1L)
                .quantity(2)
                .unitPoint(Money.of(500))
                .build();
        OrderInfo.OrderDetail savedDetail = OrderInfo.OrderDetail.builder()
                .orderId(99L)
                .userId(userId)
                .totalPoint(expectedTotal)
                .status("CREATED")
                .items(List.of(savedPinfo))
                .build();
        when(orderService.saveOrder(any(OrderCommand.SaveOrder.class)))
                .thenReturn(savedDetail);

        // when
        OrderOutput out = orderFacade.placeOrder(input);

        // then: 호출 순서 검증
        InOrder ord = inOrder(userService, inventoryService, orderService);
        ord.verify(userService).getUser(any());
        ord.verify(inventoryService).checkAndDecreaseStock(any());
        ord.verify(orderService).calculateTotal(any());
        ord.verify(orderService).createOrderEntity(any());
        ord.verify(orderService).saveOrder(any());

        // then: 결과 매핑 검증
        assertThat(out.getOrderId()).isEqualTo(99L);
        assertThat(out.getUserId()).isEqualTo(userId);
        assertThat(out.getTotalPoint()).isEqualTo(expectedTotal);
        assertThat(out.getStatus()).isEqualTo("CREATED");
        assertThat(out.getItems()).hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.getProductId()).isEqualTo(1L);
                    assertThat(item.getQuantity()).isEqualTo(2);
                    assertThat(item.getUnitPoint()).isEqualTo(Money.of(500));
                });
    }
}
