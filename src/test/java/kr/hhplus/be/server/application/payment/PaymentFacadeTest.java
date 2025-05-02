package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @Mock private OrderService orderService;
    @Mock private PointService pointService;
    @Mock private PaymentService paymentService;
    @Mock private CouponService couponService;
    @InjectMocks private PaymentFacade paymentFacade;

    @Test
    void processPayment_withoutCoupon_usesFullAmount() {
        // given
        long orderId = 10L, userId = 20L;
        // 1) stub orderService.getOrderById(...)
        Money totalPoint = Money.of(1000);
        OrderInfo.OrderDetail original = OrderInfo.OrderDetail.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPoint(totalPoint)
                .status("CREATED")
                .items(Collections.emptyList())
                .build();
        when(orderService.getOrderById(any(OrderCommand.GetOrder.class)))
                .thenReturn(original);

        // 2) build input DTO
        PaymentInput.Process input = new PaymentInput.Process();
        ReflectionTestUtils.setField(input, "orderId", orderId);
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "couponId", null);

        // 3) stub pointService.usePoint(...) → return some dummy UserPointInfo
        PointInfo.UserPointInfo dummyPointInfo = PointInfo.UserPointInfo.builder()
                .userId(userId)
                .balance(Money.of(500))
                .build();
        when(pointService.usePoint(any(PointCommand.Use.class)))
                .thenReturn(dummyPointInfo);

        // 4) stub orderService.markAsPaid(...)
        OrderInfo.OrderDetail paid = OrderInfo.OrderDetail.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPoint(totalPoint)
                .status("PAID")
                .items(Collections.emptyList())
                .build();
        when(orderService.markAsPaid(any(OrderCommand.MarkPaid.class)))
                .thenReturn(paid);

        // 5) stub paymentService.savePayment(...)
        // no discount
        PaymentInfo.PaymentResult payResult = PaymentInfo.PaymentResult.builder()
                .id(55L)
                .orderId(orderId)
                .paymentAmount(totalPoint)
                .build();
        when(paymentService.savePayment(any(PaymentCommand.SavePayment.class)))
                .thenReturn(payResult);

        // when
        PaymentOutput.Result result = paymentFacade.processPayment(input);

        // then: couponService.applyCoupon은 호출되지 않아야 함
        verify(couponService, never()).applyCoupon(any());
        // pointService.usePoint는 totalPoint로 호출
        verify(pointService).usePoint(argThat(cmd ->
                cmd.getUserId().equals(userId) &&
                        cmd.getAmount().compareTo(totalPoint) == 0
        ));
        // markAsPaid 호출
        verify(orderService).markAsPaid(argThat(cmd ->
                cmd.getOrderId().equals(orderId)
        ));
        // savePayment 호출
        verify(paymentService).savePayment(argThat(cmd ->
                cmd.getOrderId().equals(orderId) &&
                        cmd.getPaymentAmount().compareTo(totalPoint) == 0
        ));

        // 결과 검증
        assertThat(result.getId()).isEqualTo(55L);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getPaymentAmount()).isEqualTo(totalPoint.amount().longValue());
    }

    @Test
    void processPayment_withCoupon_appliesDiscount() {
        // given
        long orderId = 11L, userId = 22L, couponId = 33L;
        Money totalPoint = Money.of(2000);
        Money discount = Money.of(500);

        // Stub original order detail
        OrderInfo.OrderDetail original = OrderInfo.OrderDetail.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPoint(totalPoint)
                .status("CREATED")
                .items(Collections.emptyList())
                .build();
        when(orderService.getOrderById(any())).thenReturn(original);

        // Build input with couponId
        PaymentInput.Process input = new PaymentInput.Process();
        ReflectionTestUtils.setField(input, "orderId", orderId);
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "couponId", couponId);

        // Stub couponService.applyCoupon(...)
        CouponInfo.Discount discountInfo = CouponInfo.Discount.builder()
                .amount(discount)
                .build();
        when(couponService.applyCoupon(argThat(cmd ->
                cmd.getCouponId().equals(couponId) &&
                        cmd.getRequiredPoints().compareTo(totalPoint) == 0
        ))).thenReturn(discountInfo);

        // Stub pointService.usePoint(...)
        PointInfo.UserPointInfo dummyPointInfo = PointInfo.UserPointInfo.builder()
                .userId(userId)
                .balance(Money.of(1000))
                .build();
        when(pointService.usePoint(argThat(cmd ->
                cmd.getUserId().equals(userId) &&
                        cmd.getAmount().compareTo(totalPoint.subtract(discount)) == 0
        ))).thenReturn(dummyPointInfo);

        // Stub markAsPaid(...)
        OrderInfo.OrderDetail paid = OrderInfo.OrderDetail.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPoint(totalPoint)
                .status("PAID")
                .items(Collections.emptyList())
                .build();
        when(orderService.markAsPaid(any())).thenReturn(paid);

        // Stub paymentService.savePayment(...)
        Money payAmount = totalPoint.subtract(discount);
        PaymentInfo.PaymentResult payResult = PaymentInfo.PaymentResult.builder()
                .id(77L)
                .orderId(orderId)
                .paymentAmount(payAmount)
                .build();
        when(paymentService.savePayment(argThat(cmd ->
                cmd.getOrderId().equals(orderId) &&
                        cmd.getPaymentAmount().compareTo(payAmount) == 0
        ))).thenReturn(payResult);

        // when
        PaymentOutput.Result result = paymentFacade.processPayment(input);

        // then: couponService.applyCoupon 호출 확인
        verify(couponService).applyCoupon(any());
        // pointService.usePoint 확인
        verify(pointService).usePoint(any());
        // savePayment 확인
        verify(paymentService).savePayment(any());

        // 결과 검증
        assertThat(result.getId()).isEqualTo(77L);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getPaymentAmount()).isEqualTo(payAmount.amount().longValue());
    }
}
