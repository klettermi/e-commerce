package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.common.Money;

public record UserPointRequest(
        Long userId,
        Money pointBalance
) {}