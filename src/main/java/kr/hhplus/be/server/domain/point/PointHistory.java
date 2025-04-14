package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.point.dto.PointHistoryResponseDto;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@NoArgsConstructor
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    @Embedded
    private Money amount;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column
    private Long userId;

    public PointHistoryResponseDto toDto() {
        return new PointHistoryResponseDto(
                id,
                userId,
                amount,
                type.toString()
        );
    }

    /**
     * 정적 팩토리 메서드: 포인트 충전 이력 생성
     *
     * @param user   포인트 충전이 발생한 사용자 (User 엔티티)
     * @param amount 충전된 포인트 금액
     * @return 생성된 PointHistory 인스턴스
     */
    public static PointHistory createChargeHistory(User user, Money amount) {
        PointHistory history = new PointHistory();
        history.amount = amount;
        history.type = TransactionType.CHARGE;
        history.setUpdatedAt(LocalDateTime.now());
        history.userId = user.id;
        return history;
    }

    /**
     * 정적 팩토리 메서드: 포인트 사용 이력 생성
     *
     * @param user   포인트 사용이 발생한 사용자 (User 엔티티)
     * @param amount 사용된 포인트 금액
     * @return 생성된 PointHistory 인스턴스
     */
    public static PointHistory createUseHistory(User user, Money amount) {
        PointHistory history = new PointHistory();
        history.amount = amount;
        history.type = TransactionType.USE;
        history.setUpdatedAt(LocalDateTime.now());
        history.userId = user.id;
        return history;
    }

}
