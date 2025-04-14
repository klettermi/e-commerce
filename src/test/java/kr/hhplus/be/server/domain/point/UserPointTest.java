package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    // 수정: Money 타입으로 필드 값을 설정하도록 변경.
    private Money getPointBalance(UserPoint userPoint) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        return (Money) field.get(userPoint);
    }

    private void setPointBalance(UserPoint userPoint, BigDecimal value) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        // BigDecimal을 Money로 변환해서 설정
        field.set(userPoint, new Money(value));
    }

    private UserPoint createUserPointWithBalance(BigDecimal initialBalance) throws Exception {
        UserPoint userPoint = new UserPoint();
        setPointBalance(userPoint, initialBalance);
        return userPoint;
    }

    @Test
    void testChargePointsValid() throws Exception {
        // given: 초기 포인트 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(BigDecimal.valueOf(1000));

        // when: 500 포인트 충전
        userPoint.chargePoints(new Money(BigDecimal.valueOf(500)));

        // then: 잔액은 1000 + 500 = 1500이어야 함
        assertEquals(new Money(BigDecimal.valueOf(1500)), getPointBalance(userPoint), "충전 후 잔액은 1500이어야 합니다.");
    }

    @Test
    void testChargePointsInvalid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(BigDecimal.valueOf(1000));

        // when & then: 0 또는 음수 금액 충전 시 예외 발생
        InvalidStateException ex1 = assertThrows(InvalidStateException.class, () -> userPoint.chargePoints(Money.ZERO));
        Assertions.assertEquals("충전 포인트는 0 이상이어야 합니다.", ex1.getMessage());

        InvalidStateException ex2 = assertThrows(InvalidStateException.class, () -> userPoint.chargePoints(new Money(BigDecimal.valueOf(-100))));
        Assertions.assertEquals("금액은 0 이상이어야 합니다.", ex2.getMessage());

    }

    @Test
    void testUsePointsValid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(BigDecimal.valueOf(1000));

        // when: 400 포인트 사용
        userPoint.usePoints(new Money(BigDecimal.valueOf(400)));

        // then: 잔액은 1000 - 400 = 600이어야 함
        assertEquals(new Money(BigDecimal.valueOf(600)), getPointBalance(userPoint), "포인트 사용 후 잔액은 600이어야 합니다.");
    }

    @Test
    void testUsePointsInsufficient() throws Exception {
        // given: 초기 잔액 500
        UserPoint userPoint = createUserPointWithBalance(BigDecimal.valueOf(500));

        // when & then: 사용 포인트가 잔액보다 많을 경우 예외 발생
        InvalidStateException ex = assertThrows(InvalidStateException.class, () -> userPoint.usePoints(new Money(BigDecimal.valueOf(600))));
        assertEquals("금액은 0 이상이어야 합니다.", ex.getMessage());
    }
}
