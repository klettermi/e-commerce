package kr.hhplus.be.server.application.coupon;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER
)
@SpringBootTest
class CouponServiceConcurrencyTest {
  @Autowired
  CouponFacade couponFacade;

  @Autowired
  CouponRepository couponRepository;


  @Test
  void 동시에_쿠폰_발급_테스트() throws InterruptedException {
      // 준비
      String code = "TEST-CODE";
      int threadCount = 10;
      Coupon coupon = Coupon.builder()
              .name("ConcurrencyTestCoupon")
              .couponCode(code)
              .couponType(CouponType.AMOUNT)
              .discountAmount(Money.of(1000))
              .totalQuantity(20)
              .remainingQuantity(20)
              .build();
      User user = User.builder()
              .username("test")
              .build();

      couponRepository.save(coupon);

      // CountLatch 준비
      CountDownLatch latch = new CountDownLatch(threadCount);

      // 스레드 풀 생성 후 즉시 실행
      ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      for (int i = 0; i < threadCount; i++) {
          executor.execute(() -> {
              try {
                  couponFacade.issueCoupon(
                          new CouponInput.Issue(
                                  coupon.getId(), user.getId()
                          ));
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
      int expectedRemaining = 20 - threadCount;
      assertEquals(expectedRemaining, updatedCoupon.getRemainingQuantity(),
              "remainingQuantity는 초깃값(20)에서 발급 횟수(10)만큼 차감되어야 합니다.");

      executor.shutdown();

  }
}