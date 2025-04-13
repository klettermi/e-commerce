package kr.hhplus.be.server.interfaces.api.order.dto;

import kr.hhplus.be.server.domain.order.OrderProduct;

import java.math.BigDecimal;

public record OrderProductResponse(Long productId, int quantity, BigDecimal unitPoint) {

    public static OrderProductResponse from(OrderProduct op) {
        return new OrderProductResponse(op.getProductId(), op.getQuantity(), op.getUnitPoint());
    }
}
