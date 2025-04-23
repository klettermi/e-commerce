package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String couponCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @AttributeOverride(
            name = "discountAmount",
            column = @Column(name = "discount_amount", nullable = false)
    )
    @Column(nullable = false)
    private Money discountAmount;

    @Column(name = "discount_rate", precision = 5, scale = 4, nullable = true)
    private BigDecimal discountRate;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private int remainingQuantity;

    /**
     * 쿠폰 발급 요청 처리: 남은 쿠폰 수량이 0보다 크면 1 차감하고, 없으면 예외를 발생
     */
    public void issueCoupon() {
        if (remainingQuantity <= 0) {
            throw new DomainException.InvalidStateException("모든 쿠폰 발급이 완료되었습니다.");
        }
        remainingQuantity--;
    }

    /**
     * 정액, 정률 할인
     */
    public Money calculateDiscount(Money orderTotal) {
        return switch (couponType) {
            case AMOUNT  -> {
                if (discountAmount == null) {
                    throw new DomainException.InvalidStateException("정액 쿠폰의 할인 금액이 없습니다.");
                }
                yield discountAmount;
            }
            case PERCENT -> {
                if (discountRate == null) {
                    throw new DomainException.InvalidStateException("정률 쿠폰의 할인율이 없습니다.");
                }
                BigDecimal discounted = orderTotal.amount()
                        .multiply(discountRate)
                        .setScale(0, BigDecimal.ROUND_DOWN);
                yield new Money(discounted);
            }
        };
    }
}
