package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

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

    /**
     * 쿠폰을 조회하고, 할인액 계산 후 사용 표시
     */
    public Money applyCoupon(Long couponId, Money requiredPoints) {
        IssuedCoupon issuedCoupon = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new DomainException.EntityNotFoundException(
                        "찾을 수 없는 쿠폰입니다. couponId: " + couponId
                ));

        Money discount = issuedCoupon.getCoupon().calculateDiscount(requiredPoints);
        issuedCoupon.markAsUsed();
        couponRepository.save(issuedCoupon);

        return discount;
    }
}
