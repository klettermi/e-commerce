package kr.hhplus.be.server.interfaces.api.point.dto;

public record UsePointRequestDto(
        long userId,
        int amount
) {
}
