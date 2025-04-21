package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponServiceConcurrencyTest {

    private static final int THREAD_COUNT    = 100;
    private static final int INITIAL_QUANTITY = 50;
    private final String couponCode = "TEST50";

    @Autowired
    private CouponRepository couponRepository;
    @Autowired private CouponService couponService;

    @BeforeAll
    void setupCoupon() {
        // 초기 쿠폰 생성: 남은 수량 = INITIAL_QUANTITY
        Coupon coupon = new Coupon(couponCode, INITIAL_QUANTITY);
        couponRepository.save(coupon);
    }

    @Test
    void 동시에_쿠폰_발급시_수량_초과되지_않아야_한다() throws InterruptedException {
        // “동시 시작”용 래치
        CountDownLatch startLatch = new CountDownLatch(1);
        // “모두 완료”용 래치
        CountDownLatch doneLatch  = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount   = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();  // 동시에 시작 대기
                    couponService.issueCoupon(couponCode);
                    successCount.incrementAndGet();
                } catch (InvalidStateException e) {
                    // 남은 수량이 없을 때 던져지는 예외
                    exceptionCount.incrementAndGet();
                } catch (Exception e) {
                    // 그 외 예상치 못한 예외도 카운트
                    exceptionCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비 완료 → 동시에 시작
        startLatch.countDown();
        // 모든 작업이 끝날 때까지 대기
        doneLatch.await();
        executor.shutdown();

        // 최종 DB 상태 조회
        Coupon finalCoupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new AssertionError("Coupon이 존재해야 합니다."));

        // 1) 정상 발급된 횟수는 INITIAL_QUANTITY 이어야 한다.
        assertEquals(INITIAL_QUANTITY, successCount.get(),
                "발급 성공 횟수가 초기 수량과 달라요.");

        // 2) 남은 수량은 0 이어야 한다.
        assertEquals(0, finalCoupon.getRemainingQuantity(),
                "최종 남은 수량이 0이 아니에요.");

        // 3) 실패(예외) 횟수는 (THREAD_COUNT - INITIAL_QUANTITY) 이어야 한다.
        assertEquals(THREAD_COUNT - INITIAL_QUANTITY, exceptionCount.get(),
                "발급 실패 횟수가 예상과 다릅니다.");
    }

}