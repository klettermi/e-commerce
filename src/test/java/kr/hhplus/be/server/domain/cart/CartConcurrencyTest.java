package kr.hhplus.be.server.domain.cart;

import kr.hhplus.be.server.domain.common.Money;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CartConcurrencyTest {

    private static final int THREAD_COUNT = 20;
    private static final long USER_ID = 1L;

    /**
     * 여러 스레드가 동시에 서로 다른 CartItem 을 추가했을 때,
     * 최종 cartItems 크기가 THREAD_COUNT 와 일치해야 합니다.
     */
    @Test
    void concurrentAddItemInCart() throws InterruptedException {
        Cart cart = new Cart(USER_ID);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // 준비
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    CartItem item = CartItem.builder()
                            .productId((long) i)
                            .productName("Product " + i)
                            .quantity(1)
                            .price(Money.of(1000))
                            .build();
                    cart.addItemInCart(item);
                })
        );

        // 모두 준비되면 동시에 시작
        ready.await();
        start.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // 검증
        assertEquals(THREAD_COUNT, cart.getCartItems().size(),
                "동시 추가 후 cartItems 크기는 " + THREAD_COUNT + "이어야 합니다.");
    }

    /**
     * 여러 스레드가 동시에 같은 CartItem 을 추가·제거를 반복 실행했을 때,
     * removeItem 후에는 반드시 cartItems 에서 완전히 사라져 있어야 합니다.
     */
    @RepeatedTest(5)
    void concurrentAddAndRemoveSameItem() throws InterruptedException {
        Cart cart = new Cart(USER_ID);
        CartItem sharedItem = CartItem.builder()
                .productId(42L)
                .productName("Shared")
                .quantity(1)
                .price(Money.of(500))
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // 각 스레드가 addItemInCart, removeItem 순으로 실행
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    cart.addItemInCart(sharedItem);
                    cart.removeItem(sharedItem);
                })
        );

        // 동시 시작
        ready.await();
        start.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // 검증: 모든 스레드가 제거를 시도했으므로 최종적으로 비어 있어야 함
        assertTrue(cart.getCartItems().isEmpty(), "동시 add/remove 후 cartItems 는 비어 있어야 합니다.");
        assertNull(sharedItem.getCart(), "제거된 후 sharedItem.getCart() 는 null 이어야 합니다.");
    }
}
