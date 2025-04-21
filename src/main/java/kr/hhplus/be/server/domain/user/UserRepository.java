package kr.hhplus.be.server.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long userId);

    User save(User user);

    void deleteAll();

    long count();

    User saveAndFlush(User user);

    List<User> findAll();
}
