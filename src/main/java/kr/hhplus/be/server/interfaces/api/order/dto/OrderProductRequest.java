package kr.hhplus.be.server.interfaces.api.order.dto;

import kr.hhplus.be.server.domain.common.Money;

public record OrderProductRequest(
        Long productId,
        int quantity,
        Money unitPoint
) {
}
