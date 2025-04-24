package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        UserResponse response = UserResponse.fromEntity(user);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<?> getUserAll() {
        List<User> userList = userService.getUserAll();
        return ApiResponse.success(userList);
    }
}
