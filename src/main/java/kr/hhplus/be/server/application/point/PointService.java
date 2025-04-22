package kr.hhplus.be.server.application.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.application.common.exception.BusinessExceptionHandler;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.common.exception.ErrorCodes;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserPoint getPoint(long userId) {
        return pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));
    }

    @Transactional(readOnly = true)
    public List<PointHistory> getPointHistory(long userId) {
        return pointRepository.findByUserId(userId);
    }

    /**
     * 포인트 충전: 유효성 검사 후, 해당 사용자 포인트(UserPoint)를 갱신하고 포인트 이력을 기록합니다.
     * @param userId 충전할 사용자 ID
     * @param amount 충전 금액 (양수)
     * @return 갱신된 포인트 정보를 DTO로 반환
     */

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
        PointHistory history = PointHistory.createChargeHistory(user, amount);
        pointRepository.save(history);

        return userPoint;
    }

    /**
     * 포인트 사용: 유효성 검사와 잔액 검증 후, 포인트를 차감하고 사용 이력을 기록합니다.
     * @param userId 사용자 ID
     * @param amount 사용 금액
     * @return 사용 후 갱신된 포인트 정보를 DTO로 반환
     */

    @Transactional
    public UserPoint usePoint(Long userId, Money amount) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));


        userPoint.validate(amount, TransactionType.USE);

        userPoint.usePoints(amount);
        pointRepository.save(userPoint);

        PointHistory history = PointHistory.createUseHistory(user, amount);
        pointRepository.save(history);

        return userPoint;
    }

}
