package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @Mock private OrderService orderService;
    @Mock private PointService pointService;
    @Mock private PaymentService paymentService;
    @Mock private CouponService couponService;

    @InjectMocks private PaymentFacade paymentFacade;

    private final Long orderId = 11L;
    private final Long userId  = 22L;
    private final Long couponId= 33L;
    private final Money totalPoint = Money.of(1_000);
    private final Money discount   = Money.of(200);

    private OrderInfo.OrderDetail paidDetail;

    @BeforeEach
    void setUp() {
        // 공통 OrderDetail stub
        OrderInfo.OrderDetail originalDetail = mock(OrderInfo.OrderDetail.class);
        when(originalDetail.getTotalPoint()).thenReturn(totalPoint);
        when(orderService.getOrderById(any(OrderCommand.GetOrder.class)))
                .thenReturn(originalDetail);

        // paidDetail 은 할인·결제 후 상태 반환용
        paidDetail = mock(OrderInfo.OrderDetail.class);
        when(paidDetail.getOrderId()).thenReturn(orderId);
        when(paidDetail.getTotalPoint()).thenReturn(totalPoint.subtract(discount)); // after discount
        when(orderService.markAsPaid(any(OrderCommand.MarkPaid.class)))
                .thenReturn(paidDetail);

        // PaymentResult stub
        PaymentInfo.PaymentResult paymentResult = mock(PaymentInfo.PaymentResult.class);
        when(paymentResult.getId()).thenReturn(55L);
        when(paymentResult.getOrderId()).thenReturn(orderId);
        // 결제 금액은 paidDetail.totalPoint.minus(discount)
        when(paymentResult.getPaymentAmount()).thenReturn(paidDetail.getTotalPoint());
        when(paymentService.savePayment(any(PaymentCommand.SavePayment.class)))
                .thenReturn(paymentResult);
    }

    @Test
    void processPayment_withoutCoupon_appliesZeroDiscount() {
        // 입력 DTO 준비 (couponId == null)
        PaymentInput.Process input = new PaymentInput.Process();
        ReflectionTestUtils.setField(input, "orderId", orderId);
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "couponId", null);

        // 쿠폰 서비스는 호출되지 않아야 함
        // 포인트 사용, 결제 플로우만 stub: pointService.usePoint 은 void
        doNothing().when(pointService).usePoint(any(PointCommand.Use.class));

        // when
        PaymentOutput.Result result = paymentFacade.processPayment(input);

        // then: discount = 0 이므로 markAsPaid 의 인자로 원금(totalPoint) 그대로 전달
        verify(couponService, never()).applyCoupon(any(CouponCommand.ApplyCoupon.class));
        verify(pointService).usePoint(argThat(cmd ->
                cmd.getUserId().equals(userId)
                        && cmd.getAmount().compareTo(totalPoint) == 0
        ));
        verify(orderService).markAsPaid(argThat(cmd ->
                cmd.getOrderId().equals(orderId)
        ));
        verify(paymentService).savePayment(argThat(cmd ->
                cmd.getOrderId().equals(orderId)
                        && cmd.getPaymentAmount().compareTo(totalPoint) == 0
        ));

        // 결과 매핑 검증
        assertThat(result.getId()).isEqualTo(55L);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getPaymentAmount()).isEqualTo(totalPoint.amount().longValue());
    }

    @Test
    void processPayment_withCoupon_appliesDiscount() {
        // 입력 DTO 준비 (couponId != null)
        PaymentInput.Process input = new PaymentInput.Process();
        ReflectionTestUtils.setField(input, "orderId", orderId);
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "couponId", couponId);

        // 쿠폰 서비스 stub: 할인 금액 반환
        var couponResult = mock(Object.class, withSettings().stubOnly());
        // 동적 proxy 로 getAmount() 메서드 정의
        when(couponService.applyCoupon(any(CouponCommand.ApplyCoupon.class)))
                .thenAnswer(invocation -> new Object() {
                    public Money getAmount() { return discount; }
                });

        // pointService.usePoint stub
        doNothing().when(pointService).usePoint(any(PointCommand.Use.class));

        // when
        PaymentOutput.Result result = paymentFacade.processPayment(input);

        // then
        // 1) 쿠폰 서비스 호출
        verify(couponService).applyCoupon(argThat(cmd ->
                cmd.getCouponId().equals(couponId)
                        && cmd.getRequiredPoints().compareTo(totalPoint) == 0
        ));
        // 2) 포인트 사용시 총액에서 discount 차감
        verify(pointService).usePoint(argThat(cmd ->
                cmd.getUserId().equals(userId)
                        && cmd.getAmount().compareTo(totalPoint.subtract(discount)) == 0
        ));
        // 3) 결제 저장시에도 동일한 금액으로
        verify(paymentService).savePayment(argThat(cmd ->
                cmd.getPaymentAmount().compareTo(totalPoint.subtract(discount)) == 0
        ));

        // 결과 매핑 검증
        assertThat(result.getId()).isEqualTo(55L);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getPaymentAmount())
                .isEqualTo(paidDetail.getTotalPoint().amount().longValue());
    }
}
