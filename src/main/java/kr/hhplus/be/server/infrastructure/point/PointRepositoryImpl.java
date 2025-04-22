package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
    private final PointHistoryJpaRepository pointHistoryRepo;
    private final UserPointJpaRepository userPointRepo;

    @Override
    public List<PointHistory> findByUserId(long userId) {
        return pointHistoryRepo.findByUserId(userId);
    }

    @Override
    public Optional<UserPoint> findByUserId(Long userId) {
        return userPointRepo.findByUserId(userId);
    }

    @Override
    public void save(UserPoint userPoint) {
        userPointRepo.save(userPoint);
    }

    @Override
    public Optional<UserPoint> findById(long userId) {
        return userPointRepo.findById(userId);
    }

    @Override
    public void save(PointHistory pointHistory) {
        pointHistoryRepo.save(pointHistory);
    }

    @Override
    public void deleteAll() {
        pointHistoryRepo.deleteAll();
        userPointRepo.deleteAll();
    }

    @Override
    public Optional<UserPoint> findByIdForUpdate(Long id) {
        return userPointRepo.findByIdForUpdate(id);
    }
}
