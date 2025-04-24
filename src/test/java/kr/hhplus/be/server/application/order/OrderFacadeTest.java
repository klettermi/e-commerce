package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.InventoryCommand;
import kr.hhplus.be.server.domain.inventory.InventoryService;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock OrderService orderService;
    @Mock UserService  userService;
    @Mock InventoryService inventoryService;

    @InjectMocks OrderFacade orderFacade;

    private final Long userId    = 10L;
    private final Long orderId   = 20L;
    private final Long prodId    = 30L;
    private final int  quantity  = 2;
    private final Money totalPoints = Money.of(300);

    private OrderInfo.OrderDetail detail;
    private OrderInfo.Total       totalInfo;

    @BeforeEach
    void setUp() {
        // 공통 stub 객체 준비
        detail    = mock(OrderInfo.OrderDetail.class);
        totalInfo = mock(OrderInfo.Total.class);

        when(detail.getOrderId()).thenReturn(orderId);
        when(detail.getUserId()).thenReturn(userId);
        when(detail.getTotalPoint()).thenReturn(totalPoints);
        when(detail.getStatus()).thenReturn(OrderStatus.CREATED.name());

        // 하나의 상품 아이템
        OrderInfo.OrderProductInfo infoItem = mock(OrderInfo.OrderProductInfo.class);
        when(infoItem.getProductId()).thenReturn(prodId);
        when(infoItem.getQuantity()).thenReturn(quantity);
        when(infoItem.getUnitPoint()).thenReturn(Money.of(150));

        when(detail.getItems()).thenReturn(List.of(infoItem));
        when(totalInfo.getTotalPoint()).thenReturn(totalPoints);
    }

    @Test
    void getOrder_mapsToOrderOutput() {
        // given
        OrderInput.Get input = new OrderInput.Get();
        ReflectionTestUtils.setField(input, "orderId", orderId);

        when(orderService.getOrderById(eq(OrderCommand.GetOrder.of(orderId))))
                .thenReturn(detail);

        // when
        OrderOutput output = orderFacade.getOrder(input);

        // then
        assertThat(output.getOrderId()).isEqualTo(orderId);
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getTotalPoint()).isEqualTo(totalPoints);
        assertThat(output.getStatus()).isEqualTo(OrderStatus.CREATED);

        assertThat(output.getItems()).hasSize(1);
        var item = output.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(prodId);
        assertThat(item.getQuantity()).isEqualTo(quantity);
        assertThat(item.getUnitPoint()).isEqualTo(Money.of(150));

        verify(orderService).getOrderById(eq(OrderCommand.GetOrder.of(orderId)));
        verifyNoInteractions(userService, inventoryService);
    }

    @Test
    void placeOrder_fullFlow_mapsToOrderOutput() {
        // given: Place DTO 세팅
        OrderInput.Place input = new OrderInput.Place();
        ReflectionTestUtils.setField(input, "userId", userId);

        OrderInput.Item dtoItem = new OrderInput.Item();
        ReflectionTestUtils.setField(dtoItem, "productId", prodId);
        ReflectionTestUtils.setField(dtoItem, "quantity", quantity);

        ReflectionTestUtils.setField(input, "items", List.of(dtoItem));

        // 1) userService 검증 호출 (void, exception 없으면 통과)
        doNothing().when(userService).getUser(eq(UserCommand.GetUser.of(userId)));

        // 2) inventoryService 호출
        doNothing().when(inventoryService)
                .checkAndDecreaseStock(any(InventoryCommand.DecreaseStock.class));

        // 3) 총 포인트 계산 stub
        when(orderService.calculateTotal(any(OrderCommand.CalculateTotal.class)))
                .thenReturn(totalInfo);

        // 4) 주문 엔티티 생성 stub
        Order orderEntity = mock(Order.class);
        when(orderService.createOrderEntity(any(OrderCommand.BuildOrder.class)))
                .thenReturn(orderEntity);

        // 5) 저장 후 OrderDetail stub 반환
        when(orderService.saveOrder(any(OrderCommand.SaveOrder.class)))
                .thenReturn(detail);

        // when
        OrderOutput output = orderFacade.placeOrder(input);

        // then: 필드 매핑 검증
        assertThat(output.getOrderId()).isEqualTo(orderId);
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getTotalPoint()).isEqualTo(totalPoints);
        assertThat(output.getStatus()).isEqualTo(OrderStatus.CREATED);

        assertThat(output.getItems()).hasSize(1);
        var item = output.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(prodId);
        assertThat(item.getQuantity()).isEqualTo(quantity);

        // verify 호출 순서 및 파라미터
        InOrder inOrder = inOrder(userService, inventoryService, orderService);
        inOrder.verify(userService).getUser(eq(UserCommand.GetUser.of(userId)));
        inOrder.verify(inventoryService)
                .checkAndDecreaseStock(any(InventoryCommand.DecreaseStock.class));
        inOrder.verify(orderService)
                .calculateTotal(any(OrderCommand.CalculateTotal.class));
        inOrder.verify(orderService)
                .createOrderEntity(any(OrderCommand.BuildOrder.class));
        inOrder.verify(orderService)
                .saveOrder(any(OrderCommand.SaveOrder.class));
    }
}
