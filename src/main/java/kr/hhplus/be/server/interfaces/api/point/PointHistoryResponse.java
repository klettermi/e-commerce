package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.domain.common.Money;

public record PointHistoryResponse(
        Long id,
        Long userId,
        Money changeAmount,
        String transactionType
) { }
