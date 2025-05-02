package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.coupon.CouponOutput;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponResponse {
    private Long id;
    private Long couponId;
    private Long userId;
    private String status;

    public static CouponResponse from(CouponOutput.IssuedCoupon output) {
        return CouponResponse.builder()
                .id(output.getId())
                .couponId(output.getCouponId())
                .userId(output.getUserId())
                .status(output.getStatus())
                .build();
    }
}