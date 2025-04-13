package kr.hhplus.be.server.interfaces.api.point.dto;

import java.math.BigDecimal;

public record PointResponseDto(Long userId, BigDecimal point) {}
