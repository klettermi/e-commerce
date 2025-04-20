package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderConcurrencyTest {

    private static final int THREAD_COUNT = 100;

    /**
     * 여러 스레드가 동시에 서로 다른 OrderProduct 를 addOrderProduct() 했을 때
     * 최종 getOrderProducts().size() 가 THREAD_COUNT 와 일치해야 합니다.
     */
    @Test
    void concurrentAddOrderProduct() throws InterruptedException {
        Order order = new Order(new User(), "ORD-CONC", Money.ZERO, OrderStatus.CREATED);
        ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        IntStream.range(0, THREAD_COUNT).forEach(i ->
                exec.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    OrderProduct op = OrderProduct.builder()
                            .productId((long) i)
                            .quantity(1)
                            .unitPoint(Money.of(100))
                            .build();
                    order.addOrderProduct(op);
                })
        );

        ready.await();
        start.countDown();
        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));

        List<OrderProduct> list = order.getOrderProducts();
        assertEquals(THREAD_COUNT, list.size(),
                "동시 addOrderProduct 후 요소 수는 " + THREAD_COUNT + "이어야 합니다.");
    }

    /**
     * 두 스레드가 거의 동시에 markAsPaid() 할 때,
     * 하나는 성공하고 다른 하나는 InvalidStateException을 던져야 합니다.
     */
    @RepeatedTest(5)
    void concurrentMarkAsPaid_onlyOneSucceeds() throws InterruptedException {
        Order order = new Order(new User(), "ORD-CONC", Money.ZERO, OrderStatus.CREATED);
        ExecutorService exec = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ConcurrentLinkedQueue<Exception> errors = new ConcurrentLinkedQueue<>();

        // 두 스레드 모두 준비 → 동시에 markAsPaid 호출
        for (int i = 0; i < 2; i++) {
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    order.markAsPaid();
                } catch (InvalidStateException e) {
                    errors.add(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await();
        start.countDown();
        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));

        // 1번만 예외, 최종 상태는 PAID
        assertEquals(1, errors.size(), "동시 호출 시 하나만 예외가 발생해야 합니다.");
        assertEquals(OrderStatus.PAID, order.getStatus(), "최종 상태는 PAID여야 합니다.");
    }
}
