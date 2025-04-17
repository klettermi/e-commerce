package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPointJpaRepository extends JpaRepository<UserPoint, Long> {
    @Query("SELECT up FROM UserPoint up WHERE up.user.id = :userId")
    Optional<UserPoint> findByUserId(@Param("userId") Long userId);;
}
