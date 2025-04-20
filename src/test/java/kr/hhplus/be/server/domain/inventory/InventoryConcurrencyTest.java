package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryConcurrencyTest {

    private static final int THREAD_COUNT = 100;

    @Test
    void concurrentDecreaseStock_decrementByOne_eachThread() throws InterruptedException {
        // given: 초기 재고 THREAD_COUNT
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(THREAD_COUNT)
                .build();

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // when: 각 스레드가 동시에 1씩 차감
        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                inventory.decreaseStock(1);
            });
        }

        // 시작
        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS), "All threads should finish");

        // then: 최종 재고 0
        assertEquals(0, inventory.getQuantity(), "동시 차감 후 재고는 0이어야 합니다.");
    }

    @Test
    void concurrentDecreaseStock_insufficientStock_someThreadsThrow() throws InterruptedException {
        // given: 초기 재고 10
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger exceptionCount = new AtomicInteger();

        // when: 20 스레드가 동시에 1씩 차감 시도 (요청 총 20)
        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    inventory.decreaseStock(1);
                } catch (InvalidStateException e) {
                    exceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 시작
        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS), "All threads should finish");

        // then: 10명은 성공, 10명은 재고부족 예외
        assertEquals(10, exceptionCount.get(), "10개의 재고부족 예외가 발생해야 합니다.");
        assertTrue(inventory.getQuantity() >= 0, "재고는 음수가 아니어야 합니다.");
    }

    @Test
    void concurrentIncreaseStock_incrementByOne_eachThread() throws InterruptedException {
        // given: 초기 재고 0
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(0)
                .build();

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // when: 각 스레드가 동시에 1씩 증가
        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                inventory.increaseStock(1);
            });
        }

        // 시작
        ready.await();
        start.countDown();

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS), "All threads should finish");

        // then: 최종 재고 THREAD_COUNT
        assertEquals(THREAD_COUNT, inventory.getQuantity(), "동시 증가 후 재고는 " + THREAD_COUNT + "이어야 합니다.");
    }
}
