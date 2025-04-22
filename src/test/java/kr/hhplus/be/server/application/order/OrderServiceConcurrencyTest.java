package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(OrderServiceConcurrencyTest.InventoryMockConfig.class)
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // TestConfiguration에서 등록한 mock이 주입됩니다
    @Autowired
    private InventoryChecker inventoryChecker;

    @BeforeEach
    void setUp() {
        given(inventoryChecker.hasSufficientStock(anyLong(), anyInt()))
                .willReturn(true);
    }

    @Test
    void 동시에_여러_주문_테스트() throws InterruptedException {
        int threadCount = 10;

        // 실제 User를 저장해야 placeOrder가 정상 동작합니다
        User dummyUser = userRepository.save(
                User.builder()
                        .username("test")
                        .build()
        );

        String orderNumber = "ORD-";
        List<OrderProduct> reqs = List.of(
                OrderProduct.builder()
                        .productId(10L)
                        .quantity(2)
                        .unitPoint(Money.of(500))
                        .build(),
                OrderProduct.builder()
                        .productId(20L)
                        .quantity(3)
                        .unitPoint(Money.of(200))
                        .build()
        );

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    orderService.placeOrder(dummyUser, orderNumber + UUID.randomUUID(), reqs);
                } catch (DomainException.InvalidStateException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        List<Order> saved = orderRepository.findAll();
        assertEquals(threadCount, saved.size(), "저장된 주문 개수가 같아야 합니다.");

        var expectedTotal = reqs.stream()
                .map(r -> r.getUnitPoint().multiply(r.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        for (Order o : saved) {
            assertEquals(OrderStatus.PAID, o.getStatus());
            assertEquals(expectedTotal, o.getTotalPoint());
        }
    }

    @TestConfiguration
    static class InventoryMockConfig {
        @Bean
        public InventoryChecker inventoryChecker() {
            return org.mockito.Mockito.mock(InventoryChecker.class);
        }
    }
}
