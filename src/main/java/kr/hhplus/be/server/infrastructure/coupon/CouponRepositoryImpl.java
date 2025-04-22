package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findByCouponCode(String couponCode) {
        return couponJpaRepository.findByCouponCode(couponCode);
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
    public Optional<Coupon> findByCouponCodeForUpdate(String couponCode) {
        return couponJpaRepository.findByCouponCodeForUpdate(couponCode);
    }
}
