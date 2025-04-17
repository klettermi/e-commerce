package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserId(long userId);
}
