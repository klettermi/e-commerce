package kr.hhplus.be.server.application.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.point.PointHistoryResponse;
import kr.hhplus.be.server.interfaces.api.point.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final PointValidationService validationService;

    @Transactional(readOnly = true)
    public PointResponse getPoint(long userId) {
        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));
        return new PointResponse(userPoint.getUser().getId(), userPoint.getPointBalance());
    }

    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointHistory(long userId) {
        List<PointHistory> histories = pointRepository.findByUserId(userId);
        return histories.stream()
                .map(PointHistory::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 포인트 충전: 유효성 검사 후, 해당 사용자 포인트(UserPoint)를 갱신하고 포인트 이력을 기록합니다.
     * @param userId 충전할 사용자 ID
     * @param amount 충전 금액 (양수)
     * @return 갱신된 포인트 정보를 DTO로 반환
     */
    public PointResponse chargePoint(long userId, Money amount) {
        // 유효성 검사
        validationService.validate(amount, TransactionType.CHARGE);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        // 충전 전 현재 포인트 조회
        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new DomainException.InvalidStateException("UserPoint not found for id: " + userId));

        userPoint.chargePoints(amount);
        pointRepository.save(userPoint);

        // 포인트 충전 이력 기록
        PointHistory history = PointHistory.createChargeHistory(user, amount);
        pointRepository.save(history);

        return new PointResponse(userPoint.getUser().getId(), userPoint.getPointBalance());
    }

    /**
     * 포인트 사용: 유효성 검사와 잔액 검증 후, 포인트를 차감하고 사용 이력을 기록합니다.
     * @param userId 사용자 ID
     * @param amount 사용 금액
     * @return 사용 후 갱신된 포인트 정보를 DTO로 반환
     */
    public PointResponse usePoint(Long userId, Money amount) {
        validationService.validate(amount, TransactionType.USE);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for id: " + userId));

        if (amount.compareTo(userPoint.getPointBalance()) > 0) {
            throw new IllegalArgumentException("사용 포인트가 부족합니다.");
        }

        userPoint.usePoints(amount);
        pointRepository.save(userPoint);

        PointHistory history = PointHistory.createUseHistory(user, amount);
        pointRepository.save(history);

        return new PointResponse(userPoint.getUser().getId(), userPoint.getPointBalance());
    }
}
