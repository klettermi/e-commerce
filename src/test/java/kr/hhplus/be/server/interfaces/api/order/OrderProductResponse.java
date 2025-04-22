package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.OrderProduct;

public record OrderProductResponse(Long productId, int quantity, Money unitPoint) {

    public static OrderProductResponse from(OrderProduct op) {
        return new OrderProductResponse(op.getProductId(), op.getQuantity(), op.getUnitPoint());
    }
}
