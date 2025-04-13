package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.user.User;
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

    @Column(nullable = false)
    private BigDecimal pointBalance = BigDecimal.valueOf(0);

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public void chargePoints(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainExceptions.InvalidStateException("충전 금액은 0원 이상이어야 합니다.");
        }
        pointBalance = pointBalance.add(amount);
    }

    public void usePoints(BigDecimal amount) {
        if (pointBalance.compareTo(amount) < 0) {
            throw new DomainExceptions.InvalidStateException("사용 포인트가 부족합니다.");
        }
        pointBalance = pointBalance.subtract(amount);
    }
}
