package kr.hhplus.be.server.interfaces.api.point.dto;

import kr.hhplus.be.server.domain.common.Money;

import java.math.BigDecimal;

public record ChargePointRequestDto(
        long userId,
        Money amount
) {
}
