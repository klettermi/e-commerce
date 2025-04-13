package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * 선착순 쿠폰 발급: 주어진 couponCode에 해당하는 쿠폰을 찾고,
     * 남은 쿠폰이 있으면 발급 처리(remainingQuantity 감소)
     */
    @Transactional
    public Coupon issueCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new InvalidStateException("Coupon not found: " + couponCode));
        coupon.issueCoupon();
        return couponRepository.save(coupon);
    }
}
