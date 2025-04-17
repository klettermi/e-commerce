package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UserPointConcurrencyTest {

    private static final int THREAD_COUNT = 100;

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

    /**
     * 여러 스레드가 동시에 chargePoints()를 호출할 때,
     * 모든 충전이 반영되어 최종 잔액이 initial + THREAD_COUNT * amount 인지 확인합니다.
     */
    @Test
    void concurrentChargePoints_allSucceed() throws Exception {
        UserPoint userPoint = createUserPointWithBalance(1000);
        Money chargeAmount = Money.of(100);

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userPoint.chargePoints(chargeAmount);
                } catch (Exception ignored) {
                }
            });
        }

        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));

        // 초기 1000 + 20 * 100 = 3000
        assertEquals(Money.of(1000 + THREAD_COUNT * 100), getPointBalance(userPoint));
    }

    /**
     * 여러 스레드가 동시에 usePoints()를 호출할 때,
     * 일부 스레드는 잔액 부족으로 예외가 발생하고, 최종 잔액은 0 이상이어야 합니다.
     */
    @Test
    void concurrentUsePoints_someFail() throws Exception {
        UserPoint userPoint = createUserPointWithBalance(500);
        Money useAmount = Money.of(50);
        AtomicInteger exceptionCount = new AtomicInteger();

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userPoint.usePoints(useAmount);
                } catch (InvalidStateException e) {
                    exceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));

        // 요청량 20 * 50 = 1000, 초기 500 이므로 10번은 성공, 10번은 예외 발생
        assertEquals(THREAD_COUNT - 500 / 50, exceptionCount.get());
        assertTrue(getPointBalance(userPoint).amount().intValue() >= 0);
    }

    /**
     * 동시에 charge와 use를 섞어 호출할 때,
     * 최종 잔액 계산이 일관성 있게 유지되는지 확인합니다.
     */
    @Test
    void concurrentChargeAndUse_mixedOperations() throws Exception {
        UserPoint userPoint = createUserPointWithBalance(1000);
        Money chargeAmount = Money.of(100);
        Money useAmount = Money.of(80);
        int operations = THREAD_COUNT;
        AtomicInteger useExceptions = new AtomicInteger();

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT * 2);
        CountDownLatch ready = new CountDownLatch(operations * 2);
        CountDownLatch start = new CountDownLatch(1);

        // charge threads
        for (int i = 0; i < operations; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userPoint.chargePoints(chargeAmount);
                } catch (Exception ignored) {
                }
            });
        }
        // use threads
        for (int i = 0; i < operations; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userPoint.usePoints(useAmount);
                } catch (InvalidStateException e) {
                    useExceptions.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));

        // 최종 잔액 = 1000 + 20*100 - 성공적 사용 수*80
        int totalCharged = operations * 100;
        int successfulUses = (1000 + totalCharged - getPointBalance(userPoint).amount().intValue()) / 80;
        assertEquals(successfulUses, (operations - useExceptions.get()));
        assertTrue(getPointBalance(userPoint).amount().intValue() >= 0);
    }
}
