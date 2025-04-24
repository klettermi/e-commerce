package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    public UserPoint getPoint(long userId) {
        return pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointRepository.findByUserIdHistory(userId);
    }


    @Transactional
    public UserPoint chargePoint(long userId, Money amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        // 충전 전 현재 포인트 조회
        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new DomainException.InvalidStateException("UserPoint not found for id: " + userId));

        userPoint.validate(amount, TransactionType.CHARGE);

        userPoint.chargePoints(amount);
        pointRepository.save(userPoint);

        // 포인트 충전 이력 기록
        PointHistory history = PointHistory.createChargeHistory(user.getId(), amount);
        pointRepository.save(history);

        return userPoint;
    }


    @Transactional
    public UserPoint usePoint(Long userId, Money amount) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        UserPoint userPoint = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));


        userPoint.validate(amount, TransactionType.USE);

        userPoint.usePoints(amount);
        pointRepository.save(userPoint);

        PointHistory history = PointHistory.createUseHistory(user.getId(), amount);
        pointRepository.save(history);

        return userPoint;
    }

    public UserPoint findByUserId(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for user id: " + userId));
    }

    public UserPoint save(UserPoint userPoint) {
        return pointRepository.save(userPoint);
    }

}