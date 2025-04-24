package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointFacade {
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
        // Money.of(long) 오버로드 사용
        Money amt = Money.of(input.getAmount());
        PointInfo.UserPointInfo info = pointService.chargePoint(
                PointCommand.Charge.of(input.getUserId(), amt)
        );
        return PointOutput.UserPoint.builder()
                .userId(info.getUserId())
                .balance(info.getBalance())
                .build();
    }

    public PointOutput.UserPoint use(PointInput.Use input) {
        Money amt = Money.of(input.getAmount());
        PointInfo.UserPointInfo info = pointService.usePoint(
                PointCommand.Use.of(input.getUserId(), amt)
        );
        return PointOutput.UserPoint.builder()
                .userId(info.getUserId())
                .balance(info.getBalance())
                .build();
    }
}
