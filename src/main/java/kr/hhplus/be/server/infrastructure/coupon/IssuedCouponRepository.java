package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    List<IssuedCoupon> findAllByUserId(Long userId);
}
