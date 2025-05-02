package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.redis.SimpleLockService;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private static final long DEFAULT_TTL_MS = 5_000;  // 5초
    private final SimpleLockService lockService;
    private final PointService pointService;

    public PointOutput.UserPoint getPoint(PointInput.GetPoint input) {
        PointInfo.UserPointInfo info = pointService.getPoint(
                PointCommand.GetPoint.of(input.getUserId())
        );
        return PointOutput.UserPoint.builder()
                .userId(info.getUserId())
                .balance(info.getBalance())
                .build();
    }

    public PointOutput.HistoryList getPointHistory(PointInput.GetHistory input) {
        PointInfo.HistoryListInfo info = pointService.getPointHistory(
                PointCommand.GetHistory.of(input.getUserId())
        );
        var items = info.getHistory().stream()
                .map(h -> PointOutput.HistoryItem.builder()
                        .id(h.getId())
                        .type(h.getType())
                        .amount(h.getAmount())
                        .build())
                .collect(Collectors.toList());
        return PointOutput.HistoryList.builder()
                .userId(info.getUserId())
                .history(items)
                .build();
    }

    public PointOutput.UserPoint charge(PointInput.Charge input) {
        String lockKey = "chargePoint:" + input.getUserId();
        String uuid = UUID.randomUUID().toString();

        boolean locked = lockService.tryLock(lockKey, uuid, DEFAULT_TTL_MS);

        if (!locked) {
            throw new DomainException.InvalidStateException("포인트 충전 락 획득 실패");
        }

        try {
            Money amt = Money.of(input.getAmount());
            PointInfo.UserPointInfo info = pointService.chargePoint(
                    PointCommand.Charge.of(input.getUserId(), amt)
            );
            return PointOutput.UserPoint.builder()
                    .userId(info.getUserId())
                    .balance(info.getBalance())
                    .build();
        } finally {
            lockService.unlock(lockKey, uuid);
        }
    }

    public PointOutput.UserPoint use(PointInput.Use input) {
        String lockKey = "usePoint:" + input.getUserId();
        String uuid = UUID.randomUUID().toString();

        boolean locked = lockService.tryLock(lockKey, uuid, DEFAULT_TTL_MS);

        if (!locked) {
            throw new DomainException.InvalidStateException("포인트 사용 락 획득 실패");
        }

        try {
            Money amt = Money.of(input.getAmount());
            PointInfo.UserPointInfo info = pointService.usePoint(
                    PointCommand.Use.of(input.getUserId(), amt)
            );
            return PointOutput.UserPoint.builder()
                    .userId(info.getUserId())
                    .balance(info.getBalance())
                    .build();
        } finally {
            lockService.unlock(lockKey, uuid);
        }
    }
}
