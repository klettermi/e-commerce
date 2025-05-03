package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.InventoryCommand;
import kr.hhplus.be.server.domain.inventory.InventoryService;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserService  userService;
    private final InventoryService inventoryService;
    private final ProductService productService;

    /**
     * 주문 조회
     */
    public OrderOutput getOrder(OrderInput.Get input) {
        OrderInfo.OrderDetail detail = orderService.getOrderById(
                OrderCommand.GetOrder.of(input.getOrderId())
        );
        return toOutput(detail);
    }

    /**
     * 주문 생성 플로우
     */
    @Transactional
    public OrderOutput placeOrder(OrderInput.Place input) {
        // 1) 사용자 검증
        userService.getUser(UserCommand.GetUser.of(input.getUserId()));

        // 2) DTO → OrderProduct 리스트
        List<OrderProduct> products = input.getItems().stream()
                .map(i -> {
                    return OrderProduct.builder()
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .unitPoint(Money.of(i.getUnitPrice()))
                            .build();
                })
                .collect(Collectors.toList());

        // 3) 재고 차감
        inventoryService.checkAndDecreaseStock(
                InventoryCommand.DecreaseStock.of(products)
        );

        // 4) 총 포인트 계산
        OrderInfo.Total totalInfo = orderService.calculateTotal(
                OrderCommand.CalculateTotal.of(products)
        );

        // 5) 도메인 엔티티 생성
        Order orderEntity = orderService.createOrderEntity(
                OrderCommand.BuildOrder.of(
                        input.getUserId(),
                        products,
                        totalInfo.getTotalPoint()
                )
        );

        // 6) 저장 및 Info 반환
        OrderInfo.OrderDetail savedDetail = orderService.saveOrder(
                OrderCommand.SaveOrder.of(orderEntity)
        );

        // 7) Output 매핑
        return toOutput(savedDetail);
    }

    private OrderOutput toOutput(OrderInfo.OrderDetail d) {
        return OrderOutput.builder()
                .orderId(d.getOrderId())
                .userId(d.getUserId())
                .totalPoint(d.getTotalPoint())
                .status(d.getStatus())
                .items(d.getItems().stream()
                        .map(i -> OrderOutput.Item.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .unitPoint(i.getUnitPoint())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}