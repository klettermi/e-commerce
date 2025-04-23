package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.OrderProduct;

public record OrderProductRequest(
        Long productId,
        int quantity,
        Money unitPoint
) {
    public OrderProduct toOrderProduct() {
        return OrderProduct.builder()
                .productId(productId)
                .quantity(quantity)
                .unitPoint(unitPoint)
                .build();
    }
}
