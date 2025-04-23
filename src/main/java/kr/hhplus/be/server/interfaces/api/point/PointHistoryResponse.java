package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointHistory;

public record PointHistoryResponse(
        Long id,
        Long userId,
        Money amount,
        String transactionType
) {
    public static PointHistoryResponse fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getId(),
                pointHistory.getUserId(),
                pointHistory.getAmount(),
                pointHistory.getType().name()
        );
    }
}
