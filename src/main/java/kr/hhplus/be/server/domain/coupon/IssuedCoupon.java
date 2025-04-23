package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "issued_coupon")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssuedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    @Column
    private Long userId;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.AVAILABLE;

    public void markAsUsed() {
    }
}
