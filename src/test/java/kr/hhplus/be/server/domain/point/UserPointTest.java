package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.common.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserPointTest {

    private Money getPointBalance(UserPoint userPoint) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        return (Money) field.get(userPoint);
    }

    private void setPointBalance(UserPoint userPoint, int value) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        field.set(userPoint, Money.of(value));
    }

    private UserPoint createUserPointWithBalance(int initialBalance) throws Exception {
        UserPoint userPoint = new UserPoint();
        setPointBalance(userPoint, initialBalance);
        return userPoint;
    }

    @Test
    void testChargePointsValid() throws Exception {
        // given: 초기 포인트 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(1000);

        // when: 500 포인트 충전
        userPoint.chargePoints(Money.of(500));

        // then: 잔액은 1000 + 500 = 1500이어야 함
        assertEquals(Money.of(1500), getPointBalance(userPoint), "충전 후 잔액은 1500이어야 합니다.");
    }

    @Test
    void testChargePointsInvalid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(1000);

        // when & then: 0 또는 음수 금액 충전 시 예외 발생
        InvalidStateException ex1 = assertThrows(InvalidStateException.class, () -> userPoint.chargePoints(Money.ZERO));
        Assertions.assertEquals("충전 포인트는 0 초과이어야 합니다.", ex1.getMessage());

        InvalidStateException ex2 = assertThrows(InvalidStateException.class, () -> userPoint.chargePoints(Money.of(-100)));
        Assertions.assertEquals("금액은 0 초과이어야 합니다.", ex2.getMessage());

    }

    @Test
    void testUsePointsValid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(1000);

        // when: 400 포인트 사용
        userPoint.usePoints(Money.of(400));

        // then: 잔액은 1000 - 400 = 600이어야 함
        assertEquals(new BigDecimal(600), getPointBalance(userPoint).amount(), "포인트 사용 후 잔액은 600이어야 합니다.");
    }

    @Test
    void testUsePointsInsufficient() throws Exception {
        // given: 초기 잔액 500
        UserPoint userPoint = createUserPointWithBalance(500);

        // when & then: 사용 포인트가 잔액보다 많을 경우 예외 발생
        InvalidStateException ex = assertThrows(InvalidStateException.class, () -> userPoint.usePoints(Money.of(600)));
        assertEquals("금액은 0 초과이어야 합니다.", ex.getMessage());
    }
}
