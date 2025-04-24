package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.point.UserPointRequest;
import lombok.*;

import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Entity
@Table(name = "user_point")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "point_balance", nullable = false)
    )
    private Money pointBalance = new Money(BigDecimal.ZERO);

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Version
    private Long version;


    /**
     * 거래 유형에 따라 충전/사용 로직과 금액 검증을 수행합니다.
     */
    public void validate(Money amount, TransactionType type) {
        Money MIN_CHARGE_AMOUNT = Money.of(1000);
        Money MAX_CHARGE_AMOUNT = Money.of(100000);
        Money MIN_USE_AMOUNT = Money.of(100);
        if (type == TransactionType.CHARGE) {
            // 충전은 최소 1,000 포인트 이상, 최대 100,000 포인트 이하

            if (amount.compareTo(MIN_CHARGE_AMOUNT) < 0) {
                throw new InvalidStateException("충전 금액은 1,000 포인트 이상이어야 합니다.");
            }

            if (amount.compareTo(MAX_CHARGE_AMOUNT) > 0) {
                throw new InvalidStateException("1일 충전 금액은 최대 100,000 포인트입니다.");
            }

        } else if (type == TransactionType.USE) {
            // 사용은 최소 100 포인트 이상
            if (amount.compareTo(MIN_USE_AMOUNT) < 0) {
                throw new InvalidStateException("사용 금액은 100 포인트 이상이어야 합니다.");
            }

        } else {
            throw new InvalidStateException("유효하지 않은 거래 타입입니다.");
        }
    }

    public void chargePoints(Money amount) {
        if (amount.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStateException("충전 포인트는 0 초과이어야 합니다.");
        }
        this.pointBalance = this.pointBalance.add(amount);
    }

    public void usePoints(Money amount) {
        if (pointBalance.subtract(amount).compareTo(Money.ZERO) < 0) {
            throw new InvalidStateException("사용 포인트가 부족합니다.");
        }
        pointBalance = pointBalance.subtract(amount);
    }
}
