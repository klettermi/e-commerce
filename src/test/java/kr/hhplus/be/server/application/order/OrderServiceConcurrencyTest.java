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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER)
@EnableAspectJAutoProxy
class OrderServiceConcurrencyTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private InventoryRepository inventoryRepository;

    @BeforeEach
    void initInventory() {
        inventoryRepository.deleteAll();
        inventoryRepository.save(Inventory.builder()
                .productId(1L).quantity(1000).build());
        inventoryRepository.save(Inventory.builder()
                .productId(2L).quantity(1000).build());
    }

    @Test
    void 동시에_여러_주문_테스트() throws InterruptedException {
        int threadCount = 10;
        User dummyUser = userRepository.save(User.builder().username("test").build());

        // 1) 도메인 OrderProduct 리스트 준비
        List<OrderProduct> orderProductList = List.of(
                OrderProduct.builder()
                        .productId(1L).quantity(2).unitPoint(Money.of(500)).build(),
                OrderProduct.builder()
                        .productId(2L).quantity(3).unitPoint(Money.of(200)).build()
        );

        // 2) DTO Item 리스트로 변환
        List<OrderInput.Item> dtoItems = orderProductList.stream()
                .map(p -> new OrderInput.Item(p.getProductId(), p.getUnitPoint().amount().intValue(), p.getQuantity()))
                .toList();

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    orderFacade.placeOrder(new OrderInput.Place(dummyUser.getId(), dtoItems));
                    count.incrementAndGet();
                } catch (DomainException.InvalidStateException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 저장된 주문 개수 검증
        List<Order> saved = orderRepository.findAll();
        assertEquals(count.intValue(), saved.size(), "저장된 주문 개수가 같아야 합니다.");

        // 기대 총 포인트 계산 (도메인 OrderProduct 기준)
        Money expectedTotal = orderProductList.stream()
                .map(op -> op.getUnitPoint().multiply(op.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        // 각 주문의 상태와 총 포인트 확인
        for (Order o : saved) {
            assertEquals(OrderStatus.CREATED, o.getStatus());
            assertEquals(
                    0,
                    expectedTotal.amount().compareTo(o.getTotalPoint().amount()),
                    "총 포인트가 같아야 합니다."
            );
        }
    }
}
