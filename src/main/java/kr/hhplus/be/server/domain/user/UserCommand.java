package kr.hhplus.be.server.domain.user;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

    @Getter
    public static class GetUser {
        private final Long userId;

        private GetUser(Long userId) {
            this.userId = userId;
        }

        public static GetUser of(Long userId) {
            return new GetUser(userId);
        }
    }

    @Getter
    public static class GetAllUsers {
        // no fields for fetching all users
        private GetAllUsers() {
        }

        public static GetAllUsers of() {
            return new GetAllUsers();
        }
    }
}