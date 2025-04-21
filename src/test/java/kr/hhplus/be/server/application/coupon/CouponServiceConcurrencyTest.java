package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponServiceConcurrencyTest {
  @Autowired
  CouponService couponService;

  @Autowired
  CouponRepository couponRepository;

  @Test
  void 동시에_쿠폰_발급_테스트() throws InterruptedException {
      // 준비
      String code = "TEST-CODE";
      int threadCount = 10;
      int initialQuantity = threadCount;
      Coupon coupon = Coupon.builder()
              .couponCode(code)
              .remainingQuantity(initialQuantity)
              .build();

      couponRepository.save(coupon);

      // CountLatch 준비
      CountDownLatch latch = new CountDownLatch(threadCount);

      // 스레드 풀 생성 후 즉시 실행
      ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      for (int i = 0; i < threadCount; i++) {
          executor.execute(() -> {
              try {
                  couponService.issueCoupon(code);
              } catch (InvalidStateException e){

              } finally {
                  latch.countDown();
              }
          });
      }

      // 모든 워커가 countDown() 호출될 때까지 대기
      latch.await();

      // 검증
      Coupon updatedCoupon = couponRepository.findByCouponCode(code).orElseThrow();
      assertEquals(0, updatedCoupon.getRemainingQuantity());

      executor.shutdown();

  }
}