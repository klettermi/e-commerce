package kr.hhplus.be.server.interfaces.api.point.dto;

import java.math.BigDecimal;

public record UsePointRequestDto(
        long userId,
        BigDecimal amount
) {
}
