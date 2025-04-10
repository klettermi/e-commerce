package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.*;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿠폰 코드 혹은 이름
    @Column(nullable = false, unique = true)
    private String couponCode;

    // 총 발급 가능한 수량
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    // 남은 발급 수량
    @Column(name = "remaining_quantity", nullable = false)
    private int remainingQuantity;

    /**
     * 쿠폰 발급 요청 처리: 남은 쿠폰 수량이 0보다 크면 1 차감하고, 없으면 예외를 발생
     */
    public void issueCoupon() {
        if (remainingQuantity <= 0) {
            throw new IllegalStateException("쿠폰 발급이 완료되었습니다.");
        }
        remainingQuantity--;
    }
}
