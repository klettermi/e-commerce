package kr.hhplus.be.server.application.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouponInput {

    @Getter
    public static class Issue {
        private Long couponId;
        private Long userId;
    }

    @Getter
    public static class GetByUser {
        private Long userId;
    }
}

