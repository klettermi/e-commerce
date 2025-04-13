package kr.hhplus.be.server.interfaces.api.point.dto;

import java.math.BigDecimal;

public record PointHistoryResponseDto(
        Long id,
        Long userId,
        BigDecimal changeAmount,
        String transactionType
) { }
