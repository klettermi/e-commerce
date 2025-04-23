package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;

import java.math.BigDecimal;

public record CouponResponse(Long couponId, String name, String discountType, BigDecimal discountAmount) {
    public static CouponResponse fromIssuedCoupon(IssuedCoupon issuedCoupon) {
        return new CouponResponse(
                issuedCoupon.getCoupon().getId(),
                issuedCoupon.getCoupon().getName(),
                issuedCoupon.getCoupon().getCouponType().name(),
                issuedCoupon.getCoupon().getDiscountAmount().amount()
        );
    }
}