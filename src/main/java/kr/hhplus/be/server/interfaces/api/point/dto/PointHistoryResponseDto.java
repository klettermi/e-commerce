package kr.hhplus.be.server.interfaces.api.point.dto;

public record PointHistoryResponseDto(
        Long id,
        Long userId,
        int changeAmount,
        String transactionType
) { }
