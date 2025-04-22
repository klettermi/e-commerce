package kr.hhplus.be.server.application.order;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER
)
@EnableAspectJAutoProxy
@Import(OrderServiceConcurrencyTest.InventoryMockConfig.class)
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

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
        List<OrderProductRequest> reqs = List.of(
                new OrderProductRequest(1L, 2, Money.of(500)),
                new OrderProductRequest(2L, 3, Money.of(200))
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
                .map(r -> r.unitPoint().multiply(r.quantity()))
                .reduce(Money.ZERO, Money::add);

        for (Order o : saved) {
            assertEquals(OrderStatus.PAID, o.getStatus());
            assertTrue(
                    expectedTotal.amount().compareTo(o.getTotalPoint().amount()) == 0,
                    "금액이 같아야 합니다"
            );
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
