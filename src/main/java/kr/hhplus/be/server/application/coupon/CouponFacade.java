package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    /**
     * 쿠폰 발급
     */
    public CouponOutput.IssuedCoupon issueCoupon(CouponInput.Issue input) {
        CouponInfo.IssuedCouponInfo info = couponService.issueCoupon(
                CouponCommand.IssueCoupon.of(input.getCouponId(), input.getUserId())
        );
        return CouponOutput.IssuedCoupon.builder()
                .id(info.getId())
                .couponId(info.getCouponId())
                .userId(info.getUserId())
                .status(info.getStatus())
                .build();
    }

    /**
     * 사용자별 쿠폰 조회
     */
    public CouponOutput.IssuedCouponList getCouponsByUserId(CouponInput.GetByUser input) {
        CouponInfo.IssuedCouponList infoList = couponService.getCouponsByUserId(
                CouponCommand.GetCouponsByUser.of(input.getUserId())
        );
        var outputs = infoList.getCoupons().stream()
                .map(info -> CouponOutput.IssuedCoupon.builder()
                        .id(info.getId())
                        .couponId(info.getCouponId())
                        .userId(info.getUserId())
                        .status(info.getStatus())
                        .build())
                .toList();
        return CouponOutput.IssuedCouponList.builder()
                .userId(infoList.getUserId())
                .coupons(outputs)
                .build();
    }
}

