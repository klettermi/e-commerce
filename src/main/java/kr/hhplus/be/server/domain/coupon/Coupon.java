package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String couponCode;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private int remainingQuantity;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * 쿠폰 발급 요청 처리: 남은 쿠폰 수량이 0보다 크면 1 차감하고, 없으면 예외를 발생
     */
    public void issueCoupon() {
        if (remainingQuantity <= 0) {
            throw new DomainException.InvalidStateException("모든 쿠폰 발급이 완료되었습니다.");
        }
        remainingQuantity--;
    }
}
