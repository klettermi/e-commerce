package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private CouponRepository couponRepo;

    @Override
    public Optional<Coupon> findByCouponCode(String couponCode) {
        return couponRepo.findByCouponCode(couponCode);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponRepo.save(coupon);
    }
}
