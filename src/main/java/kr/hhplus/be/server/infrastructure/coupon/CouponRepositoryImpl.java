package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.option.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Override
    public Optional<Coupon> findByCouponCode(String couponCode) {
        return couponJpaRepository.findByCouponCode(couponCode);
    }

    @Override
    public IssuedCoupon save(IssuedCoupon coupon) {
        return issuedCouponRepository.save(coupon);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public void deleteAll() {
        couponJpaRepository.deleteAll();
    }

    @Override
    public Optional<Coupon> findByCouponCodeForUpdate(Long couponId) {
        return couponJpaRepository.findByCouponCodeForUpdate(couponId);
    }

    @Override
    public List<IssuedCoupon> findAllByUserId(Long userId) {
        return issuedCouponRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }
}
