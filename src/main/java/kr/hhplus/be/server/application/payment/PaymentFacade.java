package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderService orderService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final CouponService couponService;

    @Transactional
    public PaymentOutput.Result processPayment(PaymentInput.Process input) {
        // 1) 주문 조회 (도메인 커맨드)
        OrderInfo.OrderDetail orderDetail = orderService.getOrderById(
                OrderCommand.GetOrder.of(input.getOrderId())
        );
        // 2) 쿠폰 적용
        Money discount = (input.getCouponId() != null)
                ? couponService.applyCoupon(
                CouponCommand.ApplyCoupon.of(
                        input.getCouponId(), orderDetail.getTotalPoint()
                )
        ).getAmount()
                : Money.ZERO;
        // 3) 포인트 사용 (도메인 커맨드)
        pointService.usePoint(
                PointCommand.Use.of(
                        input.getUserId(),
                        orderDetail.getTotalPoint().subtract(discount)
                )
        );
        // 4) 주문 상태 변경 (도메인 커맨드)
        OrderInfo.OrderDetail paidDetail = orderService.markAsPaid(
                OrderCommand.MarkPaid.of(input.getOrderId())
        );
        // 5) 결제 저장 (도메인 커맨드)
        PaymentInfo.PaymentResult paymentResult = paymentService.savePayment(
                PaymentCommand.SavePayment.of(
                        paidDetail.getOrderId(),
                        paidDetail.getTotalPoint().subtract(discount)
                )
        );
        // 6) 응답 매핑
        return PaymentOutput.Result.builder()
                .id(paymentResult.getId())
                .orderId(paymentResult.getOrderId())
                .paymentAmount(paymentResult.getPaymentAmount().amount().longValue())
                .build();
    }
}
