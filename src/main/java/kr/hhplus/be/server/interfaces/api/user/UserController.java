package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId).orElseThrow(() -> new DomainException.InvalidStateException("없는 사용자입니다."));
        UserResponse response = UserResponse.fromEntity(user);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<?> getUserAll() {
        List<User> userList = userService.getUserAll();
        return ApiResponse.success(userList);
    }
}
