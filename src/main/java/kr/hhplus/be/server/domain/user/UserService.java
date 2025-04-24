package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfo.UserDetail getUser(UserCommand.GetUser command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return UserInfo.UserDetail.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    public UserInfo.UserList getUserAll(UserCommand.GetAllUsers command) {
        var users = userRepository.findAll().stream()
                .map(u -> UserInfo.UserDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .build())
                .collect(Collectors.toList());
        return UserInfo.UserList.builder()
                .users(users)
                .build();
    }
}
