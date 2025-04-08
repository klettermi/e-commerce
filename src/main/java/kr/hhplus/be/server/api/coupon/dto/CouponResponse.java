package kr.hhplus.be.server.api.coupon.dto;

public record CouponResponse(Long couponId, String name, String discountType, int discountAmount) {}