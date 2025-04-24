package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CouponInfo {

    @Getter
    @Builder
    public static class IssuedCouponInfo {
        private final Long id;
        private final Long couponId;
        private final Long userId;
        private final String status;
    }

    @Getter
    @Builder
    public static class Discount {
        private final Money amount;
    }

    @Getter
    @Builder
    public static class IssuedCouponList {
        private final Long userId;
        private final List<IssuedCouponInfo> coupons;
    }
}