package kr.hhplus.be.server.interfaces.api.point.dto;

public record ChargePointRequestDto(
        long userId,
        int amount
) {
}
