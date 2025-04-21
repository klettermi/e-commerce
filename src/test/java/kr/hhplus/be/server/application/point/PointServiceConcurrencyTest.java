package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
class PointServiceConcurrencyTest {

    private static final int THREAD_COUNT    = 20;
    private static final int INITIAL_BALANCE = 10;
    private static final Money USE_AMOUNT    = Money.of(1);

    @Autowired private PointService    pointService;
    @Autowired private UserRepository  userRepository;
    @Autowired private PointRepository pointRepository;

    private Long userId;

    @BeforeAll
    void initData() {
        // 1) 테스트용 유저 생성
        User savedUser = userRepository.save(
                User.builder()
                        .username("concurrent")
                        .build()
        );
        userId = savedUser.getId();

        // 2) 초기 UserPoint 등록 (balance = INITIAL_BALANCE)
        UserPoint up = UserPoint.builder()
                .user(savedUser)
                .pointBalance(Money.of(INITIAL_BALANCE))
                .build();
        pointRepository.save(up);
    }

    @AfterEach
    void destroyData() {
        userRepository.deleteAll();
        pointRepository.deleteAll();
    }

    @Test
    void 동시에_포인트_사용_시_잔액_초과되지_않아야_한다() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch  = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount   = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            pool.submit(() -> {
                try {
                    // 모두 대기
                    startLatch.await();

                    // 매번 fresh 엔티티로 가져와서 StaleObject 방지
                    pointService.usePoint(userId, USE_AMOUNT);
                    successCount.incrementAndGet();
                } catch (InvalidStateException | IllegalArgumentException e) {
                    // 잔액 부족 시 InvalidStateException 또는 IllegalArgument
                    exceptionCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 동시 시작 & 완료 대기
        startLatch.countDown();
        doneLatch.await();
        pool.shutdown();

        // 1) 성공은 INITIAL_BALANCE 번
        assertEquals(INITIAL_BALANCE, successCount.get(),
                "정상 사용된 횟수는 초기 잔액과 같아야 합니다.");
        // 2) 실패는 THREAD_COUNT - INITIAL_BALANCE 번
        assertEquals(THREAD_COUNT - INITIAL_BALANCE, exceptionCount.get(),
                "잔액 부족으로 실패한 횟수가 예상과 다릅니다.");

        // 3) 최종 잔액은 0
        UserPoint finalPoint = pointService.getPoint(userId);
        assertEquals(Money.ZERO, finalPoint.getPointBalance(),
                "최종 잔액은 0이어야 합니다.");

        // 4) 포인트 사용 이력 개수 == INITIAL_BALANCE
        List<PointHistory> histories = pointService.getPointHistory(userId);
        assertEquals(INITIAL_BALANCE, histories.size(),
                "이력 개수는 성공 호출 횟수와 같아야 합니다.");
    }
}
