package kr.hhplus.be.server.application.order;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER)
@EnableAspectJAutoProxy
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    // 진짜 JPA 레포지토리
    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void initInventory() {
        // 재고 테이블 초기화
        inventoryRepository.deleteAll();
        // 두 상품의 재고를 충분히 심어둡니다
        inventoryRepository.save(Inventory.builder()
                .productId(1L)
                .quantity(1000)
                .build());
        inventoryRepository.save(Inventory.builder()
                .productId(2L)
                .quantity(1000)
                .build());
    }

    @Test
    void 동시에_여러_주문_테스트() throws InterruptedException {
        int threadCount = 10;
        User dummyUser = userRepository.save(User.builder().username("test").build());
        List<OrderProduct> reqs = List.of(
                OrderProduct.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPoint(Money.of(500))
                        .build(),
                OrderProduct.builder()
                        .productId(2L)
                        .quantity(3)
                        .unitPoint(Money.of(200))
                        .build()
        );

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    orderService.placeOrder(dummyUser, "ORD-" + UUID.randomUUID(), reqs);
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
            assertEquals(
                    0,
                    expectedTotal.amount().compareTo(o.getTotalPoint().amount()),
                    "총 포인트가 같아야 합니다."
            );
        }
    }
}