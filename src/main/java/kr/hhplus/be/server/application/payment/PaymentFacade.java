package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderService orderService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final CouponService couponService;


    @Transactional
    public Payment processPayment(Long orderId, Long userId, Long couponId)
            throws InvalidStateException {
        Order order = orderService.getOrderById(orderId);

        Money discount = (couponId != null)
                ? couponService.applyCoupon(couponId, order.getTotalPoint())
                : Money.ZERO;

        return executePayment(order, userId, discount);
    }

    /**
     * 포인트 차감 → 주문 상태 변경 → 결제 이력 저장
     */
    public Payment executePayment(Order order, Long userId, Money discount) {
        // --- 1) UserPoint 조회 & 검증 ---
        var userPoint = pointService.findByUserId(userId);
        Money toUse = order.getTotalPoint().subtract(discount);
        if (toUse.compareTo(Money.ZERO) < 0) toUse = Money.ZERO;
        if (userPoint.getPointBalance().compareTo(toUse) < 0) {
            throw new InvalidStateException(
                    "올바르지 않은 포인트: required=" + order.getTotalPoint() +
                            ", available=" + userPoint.getPointBalance()
            );
        }

        // --- 2) 포인트 차감 & 저장 ---
        userPoint.usePoints(toUse);
        pointService.save(userPoint);

        // --- 3) 주문 상태 Paid로 변경 & 저장 ---
        order.markAsPaid();

        // --- 4) 결제 이력 생성 & 저장 ---
        Payment payment = Payment.builder()
                .paymentAmount(toUse)
                .order(order)
                .build();

        return paymentService.savePayment(payment);
    }
}
