package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.common.Money;

public record OrderProductRequest(
        Long productId,
        int quantity,
        Money unitPoint
) {
}
