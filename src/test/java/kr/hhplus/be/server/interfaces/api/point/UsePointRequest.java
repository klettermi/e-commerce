package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.domain.common.Money;

public record UsePointRequest(
        long userId,
        Money amount
) {
}
