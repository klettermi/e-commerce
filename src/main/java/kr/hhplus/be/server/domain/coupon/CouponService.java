package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponInfo.IssuedCouponInfo issueCoupon(CouponCommand.IssueCoupon command) {
        Long couponId = command.getCouponId();
        Long userId = command.getUserId();
        Coupon coupon = couponRepository.findByCouponCodeForUpdate(couponId)
                .orElseThrow(() -> new InvalidStateException("Coupon not found: " + couponId));
        coupon.issueCoupon();
        IssuedCoupon issued = IssuedCoupon.builder()
                .coupon(coupon)
                .userId(userId)
                .build();
        couponRepository.save(coupon);
        IssuedCoupon saved = couponRepository.save(issued);

        return CouponInfo.IssuedCouponInfo.builder()
                .id(saved.getId())
                .couponId(coupon.getId())
                .userId(userId)
                .status(saved.getStatus().name())
                .build();
    }

    public CouponInfo.IssuedCouponList getCouponsByUserId(CouponCommand.GetCouponsByUser command) {
        Long userId = command.getUserId();
        List<IssuedCoupon> list = couponRepository.findAllByUserId(userId);
        List<CouponInfo.IssuedCouponInfo> infos = list.stream()
                .map(ic -> CouponInfo.IssuedCouponInfo.builder()
                        .id(ic.getId())
                        .couponId(ic.getCoupon().getId())
                        .userId(ic.getUserId())
                        .status(ic.getStatus().name())
                        .build())
                .collect(Collectors.toList());
        return CouponInfo.IssuedCouponList.builder()
                .userId(userId)
                .coupons(infos)
                .build();
    }

    /**
     * 쿠폰을 조회하고, 할인액 계산 후 사용 표시
     */
    public CouponInfo.Discount applyCoupon(CouponCommand.ApplyCoupon command) {
        Long couponId = command.getCouponId();
        Money required = command.getRequiredPoints();
        IssuedCoupon ic = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new DomainException.EntityNotFoundException(
                        "찾을 수 없는 쿠폰입니다. couponId: " + couponId
                ));

        Money discount = ic.getCoupon().calculateDiscount(required);
        ic.markAsUsed();
        couponRepository.save(ic);

        return CouponInfo.Discount.builder()
                .amount(discount)
                .build();
    }
}
