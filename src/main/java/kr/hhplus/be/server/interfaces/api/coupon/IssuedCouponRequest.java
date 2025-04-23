package kr.hhplus.be.server.interfaces.api.coupon;

public record IssuedCouponRequest(
        Long userId,
        Long couponId
) {
}
