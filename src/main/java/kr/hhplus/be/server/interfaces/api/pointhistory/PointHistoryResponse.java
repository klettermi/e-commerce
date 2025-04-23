package kr.hhplus.be.server.interfaces.api.pointhistory;

import kr.hhplus.be.server.domain.point.PointHistory;

import java.math.BigDecimal;

public record PointHistoryResponse(Long userId, BigDecimal amount, String type) {
    public static PointHistoryResponse fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getUserId(),
                pointHistory.getAmount().amount(),
                pointHistory.getType().name()
        );
    }
}