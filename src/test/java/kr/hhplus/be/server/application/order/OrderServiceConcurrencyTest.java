package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceConcurrencyTest {

    private static final int THREAD_COUNT    = 20;
    private static final int INITIAL_STOCK   = 10;
    private static final Long PRODUCT_ID   = 1L;
    private static final String ORDER_PREFIX = "ORD-";

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private UserRepository   userRepository;

    private User testUser;

    @BeforeEach
    void initData() {
        // 1) 테스트용 유저 생성
        testUser = userRepository.save(
                User.builder()
                        .username("mi")
                        .build()
        );

        // 2) 초기 재고 세팅: PRODUCT_ID 의 수량을 INITIAL_STOCK 으로
        inventoryRepository.save(
                Inventory.builder()
                        .productId(PRODUCT_ID)
                        .quantity(INITIAL_STOCK)
                        .build()
        );
    }

    @AfterEach
    void destroyData() {
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 동시에_주문_시_재고_초과되지_않아야_한다() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch  = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount   = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    // 1) 동시에 시작 대기
                    startLatch.await();
                    User freshUser = userRepository.findById(testUser.getId())
                            .orElseThrow();
                    // 2) placeOrder 호출: 상품 1개, 단가 1포인트
                    orderService.placeOrder(
                            freshUser,
                            ORDER_PREFIX + idx,
                            List.of(new OrderProductRequest(PRODUCT_ID, 1, Money.of(1)))
                    );
                    successCount.incrementAndGet();
                } catch (InvalidStateException e) {
                    // 재고 부족으로 던져지는 예외
                    exceptionCount.incrementAndGet();
                } catch (Exception e) {
                    // 그 외 예외도 실패로 간주
                    exceptionCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 2) 모든 스레드 동시 시작
        startLatch.countDown();
        // 3) 완료 신호 대기
        doneLatch.await();
        executor.shutdown();

        // ==== 검증 ====
        // 1) 성공 횟수 == 초기 재고
        assertEquals(INITIAL_STOCK, successCount.get(),
                "정상 주문된 횟수는 초기 재고와 같아야 합니다.");
        // 2) 실패 횟수 == THREAD_COUNT - INITIAL_STOCK
        assertEquals(THREAD_COUNT - INITIAL_STOCK, exceptionCount.get(),
                "재고 부족으로 실패한 횟수가 예상과 다릅니다.");
        // 3) DB에 저장된 주문 수 == INITIAL_STOCK
        long savedOrders = orderRepository.countByUserId(testUser);
        assertEquals(INITIAL_STOCK, savedOrders,
                "DB에 저장된 주문 건수는 초기 재고와 같아야 합니다.");
        // 4) 최종 재고는 0
        int remaining = inventoryRepository.findByProductId(PRODUCT_ID)
                .get().getQuantity();
        assertEquals(0, remaining,
                "최종 재고는 0이 되어야 합니다.");
    }
}
