package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        BigDecimal totalPoint,
        OrderStatus status,
        List<OrderProductResponse> products) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPoint().amount(),
                order.getStatus(),
                order.getOrderProducts().stream().map(OrderProductResponse::from).toList()
        );
    }
}
