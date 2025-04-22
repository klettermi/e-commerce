package kr.hhplus.be.server.domain.coupon;

import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findByCouponCode(String couponCode);

    Coupon save(Coupon coupon);

    void deleteAll();

    Optional<Coupon> findByCouponCodeForUpdate(@Param("code") String couponCode);
}
