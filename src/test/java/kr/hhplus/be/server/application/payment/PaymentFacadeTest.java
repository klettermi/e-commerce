package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PointService pointService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    private final Long orderId  = 1L;
    private final Long userId   = 2L;
    private final Long couponId = 3L;

    private Order order;
    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        // 주문 객체: 총 포인트 1000, 초기 상태 CREATED
        order = new Order(userId, Money.of(1000), OrderStatus.CREATED);

        // 유저 포인트: 충분히 보유(1000)
        userPoint = new UserPoint();
        userPoint.setPointBalance(Money.of(1000));
    }

    @Test
    void processPayment_withCoupon_appliesDiscountAndPays() {
        // 할인 200을 적용
        Money discount = Money.of(200);

        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(couponService.applyCoupon(couponId, order.getTotalPoint())).thenReturn(discount);
        when(pointService.findByUserId(userId)).thenReturn(userPoint);
        when(pointService.save(userPoint)).thenReturn(userPoint);
        // 최종 결제 금액 800
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        when(paymentService.savePayment(paymentCaptor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentFacade.processPayment(orderId, userId, couponId);

        // 포인트 차감: 1000 - 200 = 800
        assertThat(result.getPaymentAmount()).isEqualTo(Money.of(800));
        // 주문 상태 PAID
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        verify(orderService).getOrderById(orderId);
        verify(couponService).applyCoupon(couponId, order.getTotalPoint());
        verify(pointService).findByUserId(userId);
        verify(pointService).save(userPoint);

        verify(paymentService).savePayment(any());
        // 저장된 Payment 객체의 필드 검증
        Payment saved = paymentCaptor.getValue();
        assertThat(saved.getOrder()).isSameAs(order);
        assertThat(saved.getPaymentAmount()).isEqualTo(Money.of(800));
    }

    @Test
    void processPayment_withoutCoupon_paysFullAmount() {
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(pointService.findByUserId(userId)).thenReturn(userPoint);
        when(pointService.save(userPoint)).thenReturn(userPoint);
        when(paymentService.savePayment(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentFacade.processPayment(orderId, userId, null);

        // 할인 0 → 결제 금액 = 1000
        assertThat(result.getPaymentAmount()).isEqualTo(order.getTotalPoint());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        verify(couponService, never()).applyCoupon(anyLong(), any());
        verify(pointService).findByUserId(userId);
        verify(paymentService).savePayment(any());
    }

    @Test
    void executePayment_insufficientPoints_throwsInvalidState() {
        // 갖고 있는 포인트 500, 결제해야 할 금액 800
        order = new Order(userId, Money.of(800), OrderStatus.CREATED);
        userPoint.setPointBalance(Money.of(500));

        when(pointService.findByUserId(userId)).thenReturn(userPoint);

        assertThatThrownBy(() ->
                paymentFacade.executePayment(order, userId, Money.ZERO)
        )
                .isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("available=");

        verify(pointService).findByUserId(userId);
        verify(pointService, never()).save(any());
        verify(paymentService, never()).savePayment(any());
    }

    @Test
    void executePayment_discountGreaterThanTotal_resultsInZeroPayment() {
        order = new Order(userId, Money.of(300), OrderStatus.CREATED);
        userPoint.setPointBalance(Money.of(100));

        when(pointService.findByUserId(userId)).thenReturn(userPoint);
        when(pointService.save(userPoint)).thenReturn(userPoint);
        when(paymentService.savePayment(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentFacade.executePayment(order, userId, Money.of(300));

        assertThat(result.getPaymentAmount()).isEqualTo(Money.ZERO);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        verify(pointService).findByUserId(userId);
        verify(pointService).save(userPoint);
        verify(paymentService).savePayment(any());
    }
}
