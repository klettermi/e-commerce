package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {
    private int getPointBalance(UserPoint userPoint) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        return (int) field.get(userPoint);
    }

    private void setPointBalance(UserPoint userPoint, int value) throws Exception {
        Field field = UserPoint.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        field.set(userPoint, value);
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
        userPoint.chargePoints(500);

        // then: 잔액은 1000 + 500 = 1500이어야 함
        assertEquals(1500, getPointBalance(userPoint), "충전 후 잔액은 1500이어야 합니다.");
    }

    @Test
    void testChargePointsInvalid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(1000);

        // when & then: 0 또는 음수 금액 충전 시 예외 발생
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> userPoint.chargePoints(0));
        assertEquals("충전 금액은 0원 이상이여야 합니다.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> userPoint.chargePoints(-100));
        assertEquals("충전 금액은 0원 이상이여야 합니다.", ex2.getMessage());
    }

    @Test
    void testUsePointsValid() throws Exception {
        // given: 초기 잔액 1000
        UserPoint userPoint = createUserPointWithBalance(1000);

        // when: 400 포인트 사용
        userPoint.usePoints(400);

        // then: 잔액은 1000 - 400 = 600이어야 함
        assertEquals(600, getPointBalance(userPoint), "포인트 사용 후 잔액은 600이어야 합니다.");
    }

    @Test
    void testUsePointsInsufficient() throws Exception {
        // given: 초기 잔액 500
        UserPoint userPoint = createUserPointWithBalance(500);

        // when & then: 사용 포인트가 잔액보다 많을 경우 예외 발생
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userPoint.usePoints(600));
        assertEquals("사용 포인트가 부족합니다.", ex.getMessage());
    }
}