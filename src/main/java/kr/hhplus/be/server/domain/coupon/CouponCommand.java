package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCommand {

    @Getter
    public static class IssueCoupon {
        private final Long couponId;
        private final Long userId;

        private IssueCoupon(Long couponId, Long userId) {
            this.couponId = couponId;
            this.userId = userId;
        }

        public static IssueCoupon of(Long couponId, Long userId) {
            return new IssueCoupon(couponId, userId);
        }
    }

    @Getter
    public static class GetCouponsByUser {
        private final Long userId;

        private GetCouponsByUser(Long userId) {
            this.userId = userId;
        }

        public static GetCouponsByUser of(Long userId) {
            return new GetCouponsByUser(userId);
        }
    }

    @Getter
    public static class ApplyCoupon {
        private final Long couponId;
        private final Money requiredPoints;

        private ApplyCoupon(Long couponId, Money requiredPoints) {
            this.couponId = couponId;
            this.requiredPoints = requiredPoints;
        }

        public static ApplyCoupon of(Long couponId, Money requiredPoints) {
            return new ApplyCoupon(couponId, requiredPoints);
        }
    }
}

