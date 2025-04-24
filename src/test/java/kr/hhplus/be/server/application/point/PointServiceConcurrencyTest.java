package kr.hhplus.be.server.application.point;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER
)
@SpringBootTest
class PointServiceConcurrencyTest {
    @Autowired
    PointFacade pointFacade;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void cleanup() {
        pointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 동시에_포인트_충전_테스트() throws InterruptedException {
        // 준비
        int threadCount = 10;
        int defaultPoint = 10000;
        int chargePoint = 1000;

        User user = User.builder()
                .username("test")
                .build();

        userRepository.save(user);

        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .pointBalance(Money.of(defaultPoint))
                .build();

        pointRepository.save(userPoint);

        // CountDownLatch 준비
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        // 스레드 풀 생성 후 즉시 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    pointFacade.chargePoint(user.getId(), Money.of(chargePoint));
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 워커가 countDown() 호출될 때 까지 대기
        latch.await();

        // 검증
        UserPoint updatedUserPoint = pointRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("없는 포인트입니다.")
        );

        Money expected = Money.of(defaultPoint)
                .add(Money.of(chargePoint).multiply(successCount.get()));
        Money actual   = updatedUserPoint.getPointBalance();

        assertEquals(0,
                expected.amount().compareTo(actual.amount()),
                () -> String.format("기댓값=%s, 실제=%s", expected, actual)
        );
    }

    @Test
    void 동시에_포인트_사용_테스트() throws InterruptedException {
        // 준비
        int threadCount = 10;
        int defaultPoint = 10000;
        int usePoint = 1000;

        User user = User.builder()
                .username("test")
                .build();

        userRepository.save(user);

        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .pointBalance(Money.of(defaultPoint))
                .build();

        pointRepository.save(userPoint);

        // CountDownLatch 준비
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        // 스레드 풀 생성 후 즉시 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    pointFacade.usePoint(user.getId(), Money.of(usePoint));
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 워커가 countDown() 호출될 때 까지 대기
        latch.await();

        // 검증
        UserPoint updatedUserPoint = pointRepository.findByUserId(user.getId()).orElseThrow(
                () -> new DomainException.EntityNotFoundException("없는 포인트입니다.")
        );
        Money expected = Money.of(defaultPoint)
                .subtract(Money.of(usePoint).multiply(successCount.get()));
        Money actual   = updatedUserPoint.getPointBalance();

        assertEquals(0,
                expected.amount().compareTo(actual.amount()),
                () -> String.format("기댓값=%s, 실제=%s", expected, actual)
        );
    }
}