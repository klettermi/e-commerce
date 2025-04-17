package kr.hhplus.be.server.infrastructure.user;

public record UserRequest(
        Long id,
        String username,
        String password
) {}
