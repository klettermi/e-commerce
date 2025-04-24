package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    /** ① 엔티티만 생성 */
    public Order createOrderEntity(OrderCommand.BuildOrder command) {
        Order order = new Order(command.getUserId(), command.getTotalPoint(), OrderStatus.CREATED);
        command.getItems().forEach(r ->
                order.addOrderProduct(
                        OrderProduct.builder()
                                .productId(r.getProductId())
                                .quantity(r.getQuantity())
                                .unitPoint(r.getUnitPoint())
                                .build()
                )
        );
        return order;
    }

    /** ② 저장 후 Info 매핑 */
    public OrderInfo.OrderDetail saveOrder(OrderCommand.SaveOrder command) {
        Order saved = orderRepository.save(command.getOrder());
        return toOrderDetail(saved);
    }

    /** ③ 조회 후 Info 매핑 */
    public OrderInfo.OrderDetail getOrderById(OrderCommand.GetOrder command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + command.getOrderId()
                ));
        return toOrderDetail(order);
    }

    /** ④ 상태 변경 후 Info 매핑 */
    public OrderInfo.OrderDetail markAsPaid(OrderCommand.MarkPaid command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + command.getOrderId()
                ));
        order.markAsPaid();
        Order saved = orderRepository.save(order);
        return toOrderDetail(saved);
    }

    /* 공통: Info 변환 */
    private OrderInfo.OrderDetail toOrderDetail(Order order) {
        return OrderInfo.OrderDetail.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalPoint(order.getTotalPoint())
                .status(order.getStatus().name())
                .items(order.getOrderProducts().stream()
                        .map(op -> OrderInfo.OrderProductInfo.builder()
                                .productId(op.getProductId())
                                .quantity(op.getQuantity())
                                .unitPoint(op.getUnitPoint())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 주문 총 포인트 계산 (커맨드 → 인포)
     */
    public OrderInfo.Total calculateTotal(OrderCommand.CalculateTotal command) {
        Money total = command.getItems().stream()
                .map(item -> item.getUnitPoint().multiply(item.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        return OrderInfo.Total.builder()
                .totalPoint(total)
                .build();
    }
}