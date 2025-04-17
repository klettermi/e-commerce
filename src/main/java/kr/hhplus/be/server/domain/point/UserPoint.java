package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.point.UserPointRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_points")
@Getter
@Setter
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

    public static UserPoint fromDto(UserPointRequest userpointRequest, User user) {
        UserPoint userPoint = new UserPoint();
        userPoint.pointBalance = userpointRequest.pointBalance();
        userPoint.user = user;
        return userPoint;
    }

    public void chargePoints(Money amount) {
        if (amount.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainExceptions.InvalidStateException("충전 포인트는 0 이상이어야 합니다.");
        }
        this.pointBalance = new Money(this.pointBalance.amount().add(amount.amount()));
    }

    public void usePoints(Money amount) {
        if (pointBalance.subtract(amount).compareTo(Money.ZERO) < 0) {
            throw new DomainExceptions.InvalidStateException("사용 포인트가 부족합니다.");
        }
        pointBalance = pointBalance.subtract(amount);
    }
}
