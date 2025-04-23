package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.user.User;

public record UserResponse(Long id, String username) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }
}