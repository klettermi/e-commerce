package kr.hhplus.be.server.interfaces.api.order.dto;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.OrderProduct;

import java.math.BigDecimal;

public record OrderProductResponse(Long productId, int quantity, Money unitPoint) {

    public static OrderProductResponse from(OrderProduct op) {
        return new OrderProductResponse(op.getProductId(), op.getQuantity(), op.getUnitPoint());
    }
}
