package kr.hhplus.be.server.interfaces.api.point.dto;

public record UserPointRequestDto(
        long userId,
        int amount
) {
}
