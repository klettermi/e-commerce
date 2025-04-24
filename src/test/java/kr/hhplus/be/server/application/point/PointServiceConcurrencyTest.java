package kr.hhplus.be.server.application.point;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER)
class PointServiceConcurrencyTest {

    @Autowired private PointFacade pointFacade;
    @Autowired private PointRepository pointRepository;
    @Autowired private UserRepository userRepository;

    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void cleanup() {
        pointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 동시에_포인트_충전_테스트() throws InterruptedException {
        int threadCount   = 10;
        int defaultPoint  = 10_000;
        int chargeAmount  = 1_000;

        // --- 준비: 사용자 및 초기 포인트 저장 ---
        User user = userRepository.save(User.builder().username("test").build());
        UserPoint up = UserPoint.builder()
                .user(user)
                .pointBalance(Money.of(defaultPoint))
                .build();
        pointRepository.save(up);

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        ExecutorService ex = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            ex.execute(() -> {
                try {
                    // DTO 생성 및 필드 세팅
                    PointInput.Charge input = new PointInput.Charge();
                    ReflectionTestUtils.setField(input, "userId", user.getId());
                    ReflectionTestUtils.setField(input, "amount", chargeAmount);
                    // 호출
                    pointFacade.charge(input);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        ex.shutdown();

        // 최종 조회
        UserPoint updated = pointRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("없는 포인트입니다."));

        Money expected = Money.of(defaultPoint)
                .add(Money.of(chargeAmount).multiply(successCount.get()));
        Money actual = updated.getPointBalance();

        assertEquals(0,
                expected.amount().compareTo(actual.amount()),
                () -> String.format("기댓값=%s, 실제=%s", expected, actual)
        );
    }

    @Test
    void 동시에_포인트_사용_테스트() throws InterruptedException {
        int threadCount  = 10;
        int defaultPoint = 10_000;
        int useAmount    = 1_000;

        // --- 준비: 사용자 및 초기 포인트 저장 ---
        User user = userRepository.save(User.builder().username("test").build());
        UserPoint up = UserPoint.builder()
                .user(user)
                .pointBalance(Money.of(defaultPoint))
                .build();
        pointRepository.save(up);

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        ExecutorService ex = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            ex.execute(() -> {
                try {
                    // DTO 생성 및 필드 세팅
                    PointInput.Use input = new PointInput.Use();
                    ReflectionTestUtils.setField(input, "userId", user.getId());
                    ReflectionTestUtils.setField(input, "amount", useAmount);
                    // 호출
                    pointFacade.use(input);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        ex.shutdown();

        // 최종 조회
        UserPoint updated = pointRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("없는 포인트입니다."));

        Money expected = Money.of(defaultPoint)
                .subtract(Money.of(useAmount).multiply(successCount.get()));
        Money actual = updated.getPointBalance();

        assertEquals(0,
                expected.amount().compareTo(actual.amount()),
                () -> String.format("기댓값=%s, 실제=%s", expected, actual)
        );
    }
}
