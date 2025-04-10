package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.point.dto.PointHistoryResponseDto;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final PointValidationService validationService;

    @Transactional(readOnly = true)
    public PointResponseDto getPoint(long userId) {
        UserPoint userPoint = userPointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserPoint not found for id: " + userId));
        return new PointResponseDto(userPoint.getId(), userPoint.getPointBalance());
    }

    @Transactional(readOnly = true)
    public List<PointHistoryResponseDto> getPointHistory(long userId) {
        List<PointHistory> histories = pointHistoryRepository.findByUserId(userId);
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
    public PointResponseDto chargePoint(long userId, int amount) {
        // 유효성 검사
        validationService.validate(amount, TransactionType.CHARGE);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found for id: " + userId));

        // 충전 전 현재 포인트 조회
        UserPoint userPoint = userPointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserPoint not found for id: " + userId));

        // 포인트 갱신: (실제 도메인 로직은 UserPoint 내의 chargePoints 메서드로 위임할 수 있습니다.)
        userPoint.chargePoints(amount);
        userPointRepository.save(userPoint);

        // 포인트 충전 이력 기록
        PointHistory history = PointHistory.createChargeHistory(user, amount);
        pointHistoryRepository.save(history);

        return new PointResponseDto(userPoint.getId(), userPoint.getPointBalance());
    }

    /**
     * 포인트 사용: 유효성 검사와 잔액 검증 후, 포인트를 차감하고 사용 이력을 기록합니다.
     * @param userId 사용자 ID
     * @param amount 사용 금액
     * @return 사용 후 갱신된 포인트 정보를 DTO로 반환
     */
    public PointResponseDto usePoint(Long userId, int amount) {
        validationService.validate(amount, TransactionType.USE);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found for id: " + userId));

        UserPoint userPoint = userPointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserPoint not found for id: " + userId));

        if (amount > userPoint.getPointBalance()) {
            throw new IllegalArgumentException("사용 포인트가 부족합니다.");
        }

        userPoint.usePoints(amount);
        userPointRepository.save(userPoint);

        PointHistory history = PointHistory.createUseHistory(user, amount);
        pointHistoryRepository.save(history);

        return new PointResponseDto(userPoint.getId(), userPoint.getPointBalance());
    }
}
