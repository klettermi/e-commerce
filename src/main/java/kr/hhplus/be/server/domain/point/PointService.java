package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository  userRepository;

    /**
     * 현재 잔액 조회 (커맨드 → Info)
     */
    public PointInfo.UserPointInfo getPoint(PointCommand.GetPoint command) {
        UserPoint up = pointRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found: " + command.getUserId()));
        return PointInfo.UserPointInfo.builder()
                .userId(up.getUser().getId())
                .balance(up.getPointBalance())
                .build();
    }

    /**
     * 이력 조회 (커맨드 → Info)
     */
    public PointInfo.HistoryListInfo getPointHistory(PointCommand.GetHistory command) {
        List<PointHistory> history = pointRepository.findByUserIdHistory(command.getUserId());
        List<PointInfo.HistoryItemInfo> items = history.stream()
                .map(h -> PointInfo.HistoryItemInfo.builder()
                        .id(h.getId())
                        .type(h.getType().name())
                        .amount(h.getAmount())
                        .build())
                .collect(Collectors.toList());
        return PointInfo.HistoryListInfo.builder()
                .userId(command.getUserId())
                .history(items)
                .build();
    }

    /**
     * 포인트 충전 (커맨드 → Info)
     */
    @Transactional
    public PointInfo.UserPointInfo chargePoint(PointCommand.Charge command) {
        // 사용자 검증
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + command.getUserId()));

        // UserPoint 조회
        UserPoint up = pointRepository.findById(command.getUserId())
                .orElseThrow(() -> new DomainException.InvalidStateException(
                        "UserPoint not found: " + command.getUserId()));

        // 유효성 검사 & 충전
        up.validate(command.getAmount(), TransactionType.CHARGE);
        up.chargePoints(command.getAmount());
        pointRepository.save(up);

        // 이력 기록
        PointHistory hist = PointHistory.createChargeHistory(user.getId(), command.getAmount());
        pointRepository.save(hist);

        return PointInfo.UserPointInfo.builder()
                .userId(up.getUser().getId())
                .balance(up.getPointBalance())
                .build();
    }

    /**
     * 포인트 사용 (커맨드 → Info)
     */
    @Transactional
    public PointInfo.UserPointInfo usePoint(PointCommand.Use command) {
        // 사용자 검증
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + command.getUserId()));

        // UserPoint 조회
        UserPoint up = pointRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found: " + command.getUserId()));

        // 유효성 검사 & 사용
        up.validate(command.getAmount(), TransactionType.USE);
        up.usePoints(command.getAmount());
        pointRepository.save(up);

        // 이력 기록
        PointHistory hist = PointHistory.createUseHistory(user.getId(), command.getAmount());
        pointRepository.save(hist);

        return PointInfo.UserPointInfo.builder()
                .userId(up.getUser().getId())
                .balance(up.getPointBalance())
                .build();
    }
}
