package kr.hhplus.be.server.interfaces.api.point.dto;

import java.math.BigDecimal;

public record ChargePointRequestDto(
        long userId,
        BigDecimal amount
) {
}
