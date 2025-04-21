package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.common.exception.BusinessExceptionHandler;
import kr.hhplus.be.server.domain.common.exception.ErrorCodes;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
@EnableRetry
public class CouponService {

    private final CouponRepository CouponRepository;

    /**
     * 선착순 쿠폰 발급: 주어진 couponCode에 해당하는 쿠폰을 찾고,
     * 남은 쿠폰이 있으면 발급 처리(remainingQuantity 감소)
     */
    @Retryable(
            value = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public Coupon issueCoupon(String couponCode) {
        Coupon coupon = CouponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new InvalidStateException("Coupon not found: " + couponCode));
        coupon.issueCoupon();
        return CouponRepository.save(coupon);
    }

    @Recover
    public Coupon recover(ObjectOptimisticLockingFailureException retryEx, String couponCode) {
        throw new BusinessExceptionHandler(
                ErrorCodes.CONCURRENCY_COMFLICT_NOT_RESOLVED,
                "동시성 충돌로 쿠폰 발급 실패 (code=" + couponCode + ")",
                retryEx.getStackTrace()
        );
    }
}
