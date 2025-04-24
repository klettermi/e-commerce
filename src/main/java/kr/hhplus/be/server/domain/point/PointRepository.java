package kr.hhplus.be.server.domain.point;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository {
    List<PointHistory> findByUserIdHistory(long userId);

    Optional<UserPoint> findByUserId(@Param("userId") Long userId);;

    UserPoint save(UserPoint userPoint);

    Optional<UserPoint> findById(long userId);

    PointHistory save(PointHistory pointHistory);

    void deleteAll();
}
