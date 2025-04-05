package kr.hhplus.be.server.domain.coupon.dto;

public record CouponResponse(Long couponId, String name, String discountType, int discountAmount) {}