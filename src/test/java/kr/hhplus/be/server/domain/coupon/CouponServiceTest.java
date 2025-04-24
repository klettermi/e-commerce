package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private final Long COUPON_ID = 10L;
    private final Long USER_ID = 42L;
    private final Money REQUIRED_POINTS = Money.of(1000);
    private final Money DISCOUNT_AMOUNT = Money.of(300);

    private Coupon couponEntity;
    private IssuedCoupon issuedCouponEntity;

    @BeforeEach
    void setUp() {
        // Coupon 엔티티는 @Builder를 사용한다고 가정
        couponEntity = Coupon.builder()
                .id(COUPON_ID)
                .name("테스트 쿠폰")
                .couponCode("TESTCODE")
                .couponType(CouponType.AMOUNT)
                .discountAmount(Money.of(1000))
                .discountRate(null)
                .totalQuantity(10)
                .remainingQuantity(5)
                .build();

        // IssuedCoupon 엔티티도 @Builder 사용
        issuedCouponEntity = IssuedCoupon.builder()
                .id(100L)
                .coupon(couponEntity)
                .userId(USER_ID)
                .status(CouponStatus.AVAILABLE)
                .build();
    }

    @Test
    void issueCoupon_success() {
        // given
        when(couponRepository.findByCouponCodeForUpdate(COUPON_ID))
                .thenReturn(Optional.of(couponEntity));
        // save는 인자로 받은 객체를 그대로 리턴
        when(couponRepository.save(couponEntity)).thenReturn(couponEntity);
        ArgumentCaptor<IssuedCoupon> issuedCaptor = ArgumentCaptor.forClass(IssuedCoupon.class);
        when(couponRepository.save(issuedCaptor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        IssuedCoupon result = couponService.issueCoupon(COUPON_ID, USER_ID);

        // then
        assertEquals(couponEntity, result.getCoupon());
        assertEquals(USER_ID, result.getUserId());
        assertFalse(result.isUsed());

        verify(couponRepository).findByCouponCodeForUpdate(COUPON_ID);
        verify(couponRepository).save(couponEntity);
        verify(couponRepository).save(any(IssuedCoupon.class));
    }

    @Test
    void issueCoupon_notFound_throwsInvalidState() {
        when(couponRepository.findByCouponCodeForUpdate(COUPON_ID))
                .thenReturn(Optional.empty());

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> couponService.issueCoupon(COUPON_ID, USER_ID)
        );
        assertTrue(ex.getMessage().contains("Coupon not found"));
        verify(couponRepository).findByCouponCodeForUpdate(COUPON_ID);
        verify(couponRepository, never()).save((IssuedCoupon) any());
    }

    @Test
    void getCouponsByUserId_returnsList() {
        List<IssuedCoupon> stubList = List.of(issuedCouponEntity);
        when(couponRepository.findAllByUserId(USER_ID))
                .thenReturn(stubList);

        List<IssuedCoupon> result = couponService.getCouponsByUserId(USER_ID);

        assertSame(stubList, result);
        verify(couponRepository).findAllByUserId(USER_ID);
    }

    @Test
    void applyCoupon_success() {
        // given: IssuedCoupon 및 내부 Coupon 모킹
        Coupon mockCoupon = mock(Coupon.class);
        when(mockCoupon.calculateDiscount(REQUIRED_POINTS))
                .thenReturn(DISCOUNT_AMOUNT);

        IssuedCoupon mockIssued = mock(IssuedCoupon.class);
        when(mockIssued.getCoupon()).thenReturn(mockCoupon);

        when(couponRepository.findByCouponId(COUPON_ID))
                .thenReturn(Optional.of(mockIssued));
        when(couponRepository.save(mockIssued)).thenReturn(mockIssued);

        // when
        Money discount = couponService.applyCoupon(COUPON_ID, REQUIRED_POINTS);

        // then
        assertEquals(DISCOUNT_AMOUNT, discount);
        verify(mockIssued).markAsUsed();
        verify(couponRepository).findByCouponId(COUPON_ID);
        verify(couponRepository).save(mockIssued);
    }

    @Test
    void applyCoupon_notFound_throwsEntityNotFound() {
        when(couponRepository.findByCouponId(COUPON_ID))
                .thenReturn(Optional.empty());

        DomainException.EntityNotFoundException ex = assertThrows(
                DomainException.EntityNotFoundException.class,
                () -> couponService.applyCoupon(COUPON_ID, REQUIRED_POINTS)
        );
        assertTrue(ex.getMessage().contains("찾을 수 없는 쿠폰입니다"));
        verify(couponRepository).findByCouponId(COUPON_ID);
        verify(couponRepository, never()).save((IssuedCoupon) any());
    }
}
