package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_points")
@Getter
@Setter
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int pointBalance = 0;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public void chargePoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0원 이상이어야 합니다.");
        }
        pointBalance += amount;
    }

    public void usePoints(int amount) {
        if (amount > pointBalance) {
            throw new IllegalArgumentException("사용 포인트가 부족합니다.");
        }
        pointBalance -= amount;
    }
}
