package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Optional;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    private CouponJpaRepository CouponRepository;
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        CouponRepository = mock(CouponJpaRepository.class);
        couponService = new CouponService(CouponRepository);
    }

    @Test
    void issueCoupon_정상발급() {
        // given: 총 5개 중 3개 남은 쿠폰
        Coupon coupon = Coupon.builder()
                .id(1L)
                .couponCode("FIRST100")
                .totalQuantity(5)
                .remainingQuantity(3)
                .build();

        when(CouponRepository.findByCouponCode("FIRST100")).thenReturn(Optional.of(coupon));
        when(CouponRepository.save(ArgumentMatchers.any(Coupon.class))).thenAnswer(i -> i.getArgument(0));

        // when: 쿠폰 발급 요청
        Coupon issuedCoupon = couponService.issueCoupon("FIRST100");

        // then: remainingQuantity가 1 감소되어 2가 되어야 합니다.
        assertEquals(2, issuedCoupon.getRemainingQuantity(), "발급 후 남은 수량은 2여야 합니다.");
        verify(CouponRepository, times(1)).save(coupon);
    }

    @Test
    void issueCoupon_쿠폰없음_예외발생() {
        // given: 쿠폰 코드에 해당하는 쿠폰이 없는 경우
        when(CouponRepository.findByCouponCode("NOCOUPON")).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidStateException.class, () -> {
            couponService.issueCoupon("NOCOUPON");
        });
        assertTrue(exception.getMessage().contains("Coupon not found"));
    }

    @Test
    void issueCoupon_발급가능수량없음_예외발생() {
        // given: 남은 쿠폰이 0인 경우
        Coupon coupon = Coupon.builder()
                .id(1L)
                .couponCode("FIRST100")
                .totalQuantity(5)
                .remainingQuantity(0)
                .build();

        when(CouponRepository.findByCouponCode("FIRST100")).thenReturn(Optional.of(coupon));

        Exception exception = assertThrows(InvalidStateException.class, () -> {
            couponService.issueCoupon("FIRST100");
        });
        assertTrue(exception.getMessage().contains("쿠폰 발급이 완료되었습니다"));
    }
}