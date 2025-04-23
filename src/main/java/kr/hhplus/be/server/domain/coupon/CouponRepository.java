package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.option.Option;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByCouponCode(String couponCode);

    Optional<Coupon> findById(Long couponId);

    IssuedCoupon save(IssuedCoupon coupon);

    Coupon save(Coupon coupon);

    void deleteAll();

    Optional<Coupon> findByCouponCodeForUpdate(@Param("id") Long couponId);

    List<IssuedCoupon> findAllByUserId(Long userId);

    Optional<IssuedCoupon> findByCouponId(Long couponId);
}
