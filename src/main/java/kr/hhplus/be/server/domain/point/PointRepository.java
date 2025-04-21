package kr.hhplus.be.server.domain.point;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository {
    List<PointHistory> findByUserId(long userId);

    Optional<UserPoint> findByUserId(@Param("userId") Long userId);;

    void save(UserPoint userPoint);

    Optional<UserPoint> findById(long userId);

    void save(PointHistory pointHistory);

    void deleteAll();
}
