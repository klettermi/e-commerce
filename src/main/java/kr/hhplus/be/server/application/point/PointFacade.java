package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;

    public UserPoint chargePoint(long userId, Money amount) {
        return pointService.chargePoint(userId, amount);
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointService.getPointHistory(userId);
    }

    public UserPoint getPoint(long userId) {
        return pointService.getPoint(userId);
    }

    public UserPoint usePoint(long userId, Money amount) {
        return pointService.usePoint(userId, amount);
    }
}
