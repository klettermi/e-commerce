package kr.hhplus.be.server.application.coupon;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CouponOutput {

    @Getter
    @Builder
    public static class IssuedCoupon {
        private Long id;
        private Long couponId;
        private Long userId;
        private String status;
    }

    @Getter
    @Builder
    public static class IssuedCouponList {
        private Long userId;
        private List<IssuedCoupon> coupons;
    }
}
