package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.infrastructure.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.interfaces.api.coupon.IssuedCouponRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * 선착순 쿠폰 발급: 주어진 couponCode에 해당하는 쿠폰을 찾고,
     * 남은 쿠폰이 있으면 발급 처리(remainingQuantity 감소)
     */
    @Transactional
    public IssuedCoupon issueCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findByCouponCodeForUpdate(couponId)
                .orElseThrow(() -> new InvalidStateException("Coupon not found: " + couponId));
        coupon.issueCoupon();
        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .coupon(coupon)
                .userId(userId)
                .build();
        couponRepository.save(coupon);
        couponRepository.save(issuedCoupon);
        return issuedCoupon;
    }

    public List<IssuedCoupon> getCouponsByUserId(Long userId) {
        return couponRepository.findAllByUserId(userId);
    }
}
