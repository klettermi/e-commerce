package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.UserPoint;

public record PointResponse(Long userId, Money point) {
    public static PointResponse from(UserPoint userPoint) {
        return new PointResponse(
                userPoint.getUser().getId(),
                userPoint.getPointBalance()
        );
    }
}
