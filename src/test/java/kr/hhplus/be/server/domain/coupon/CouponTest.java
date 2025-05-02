package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @Test
    void issueCoupon_decrementsRemainingQuantity() {
        // given
        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("TestCoupon")
                .couponCode("CODE")
                .couponType(CouponType.AMOUNT)
                .discountAmount(Money.of(1000))
                .totalQuantity(5)
                .remainingQuantity(2)
                .build();

        // when
        coupon.issueCoupon();

        // then
        assertEquals(1, coupon.getRemainingQuantity());
    }

    @Test
    void issueCoupon_whenNoneRemaining_throwsInvalidStateException() {
        // given: remainingQuantity == 0
        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("EmptyCoupon")
                .couponCode("EMPTY")
                .couponType(CouponType.AMOUNT)
                .discountAmount(Money.of(1000))
                .totalQuantity(5)
                .remainingQuantity(0)
                .build();

        // when / then
        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                coupon::issueCoupon
        );
        assertEquals("모든 쿠폰 발급이 완료되었습니다.", ex.getMessage());
    }

    @Test
    void calculateDiscount_amountCoupon_returnsDiscountAmount() {
        // given
        Money amount = Money.of(500);
        Coupon coupon = Coupon.builder()
                .id(2L)
                .name("Fix500")
                .couponCode("FIX500")
                .couponType(CouponType.AMOUNT)
                .discountAmount(amount)
                .totalQuantity(10)
                .remainingQuantity(10)
                .build();

        // when
        Money discount = coupon.calculateDiscount(Money.of(2000));

        // then
        assertEquals(amount, discount);
    }

    @Test
    void calculateDiscount_amountCouponWithoutDiscountAmount_throwsInvalidStateException() {
        // given: discountAmount == null
        Coupon coupon = Coupon.builder()
                .id(3L)
                .name("BadAmount")
                .couponCode("BADAMT")
                .couponType(CouponType.AMOUNT)
                .discountAmount(null)
                .totalQuantity(1)
                .remainingQuantity(1)
                .build();

        // when / then
        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> coupon.calculateDiscount(Money.of(1000))
        );
        assertEquals("정액 쿠폰의 할인 금액이 없습니다.", ex.getMessage());
    }

    @Test
    void calculateDiscount_percentCoupon_returnsRoundedDownMoney() {
        // given: 25% 할인
        BigDecimal rate = new BigDecimal("0.25");
        Coupon coupon = Coupon.builder()
                .id(4L)
                .name("25Percent")
                .couponCode("PCT25")
                .couponType(CouponType.PERCENT)
                .discountRate(rate)
                .totalQuantity(5)
                .remainingQuantity(5)
                .build();
        Money orderTotal = Money.of(1234);
        // 1234 * 0.25 = 308.5 -> ROUND_DOWN -> 308

        // when
        Money discount = coupon.calculateDiscount(orderTotal);

        // then
        assertEquals(Money.of(308), discount);
    }

    @Test
    void calculateDiscount_percentCouponWithoutDiscountRate_throwsInvalidStateException() {
        // given: discountRate == null
        Coupon coupon = Coupon.builder()
                .id(5L)
                .name("BadPercent")
                .couponCode("BADPCT")
                .couponType(CouponType.PERCENT)
                .discountRate(null)
                .totalQuantity(1)
                .remainingQuantity(1)
                .build();

        // when / then
        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> coupon.calculateDiscount(Money.of(1000))
        );
        assertEquals("정률 쿠폰의 할인율이 없습니다.", ex.getMessage());
    }
}
