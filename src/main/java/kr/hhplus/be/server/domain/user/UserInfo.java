package kr.hhplus.be.server.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserInfo {

    @Getter
    @Builder
    public static class UserDto {
        private final Long id;
        private final String username;
    }

    @Getter
    @Builder
    public static class UserDetail {
        private final Long id;
        private final String username;
    }

    @Getter
    @Builder
    public static class UserList {
        private final List<UserDto> users;
    }
}
