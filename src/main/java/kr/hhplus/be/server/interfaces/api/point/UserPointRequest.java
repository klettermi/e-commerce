package kr.hhplus.be.server.interfaces.api.point;

public record UserPointRequest(
        long userId,
        int amount
) {
}
