package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;

    public IssuedCoupon issueCoupon(Long couponId, Long userId) {
        return couponService.issueCoupon(couponId, userId);
    }

    public List<IssuedCoupon> getCouponsByUserId(Long userId) {
        return couponService.getCouponsByUserId(userId);
    }
}
