package kr.hhplus.be.server.interfaces.api.coupon;

public record CouponResponse(Long couponId, String name, String discountType, int discountAmount) {}