package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CouponFacadeTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponFacade couponFacade;

    private final Long couponId = 10L;
    private final Long userId   = 42L;

    private IssuedCoupon issuedCoupon;
    private List<IssuedCoupon> issuedList;

    @BeforeEach
    void setUp() {
        issuedCoupon = IssuedCoupon.builder()
                .id(100L)
                .coupon(null)    // 세부 내용은 관심 없음
                .userId(userId)
                .build();

        issuedList = List.of(issuedCoupon);
    }

    @Test
    void issueCoupon_delegatesToService() {
        when(couponService.issueCoupon(couponId, userId))
                .thenReturn(issuedCoupon);

        IssuedCoupon result = couponFacade.issueCoupon(couponId, userId);

        assertThat(result).isSameAs(issuedCoupon);
        verify(couponService).issueCoupon(couponId, userId);
    }

    @Test
    void getCouponsByUserId_delegatesToService() {
        when(couponService.getCouponsByUserId(userId))
                .thenReturn(issuedList);

        List<IssuedCoupon> result = couponFacade.getCouponsByUserId(userId);

        assertThat(result).isSameAs(issuedList);
        verify(couponService).getCouponsByUserId(userId);
    }
}
