package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;
import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public Payment processPayment(Long orderId, Long userId, Long couponId) throws InvalidStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        UserPoint userPoint = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for user id: " + userId));

        Money requiredPoints = order.getTotalPoint();

        Money discount = Money.ZERO;
        IssuedCoupon issuedCoupon = null;
        if (couponId != null) {
            issuedCoupon = couponRepository.findByCouponId(couponId).orElseThrow(
                    () -> new EntityNotFoundException("찾을 수 없는 쿠폰입니다. couponId: " + couponId)
            );
            discount = issuedCoupon.getCoupon().calculateDiscount(requiredPoints);
            issuedCoupon.markAsUsed();
            couponRepository.save(issuedCoupon);
        }

        Money toUse = requiredPoints.subtract(discount);
        if (toUse.compareTo(Money.ZERO) < 0) {
            toUse = Money.ZERO;
        }

        if (userPoint.getPointBalance().compareTo(toUse) < 0) {
            throw new InvalidStateException("올바르지 않은 포인트: required " + requiredPoints +
                    ", available " + userPoint.getPointBalance());
        }

        userPoint.usePoints(toUse);
        pointRepository.save(userPoint);

        order.markAsPaid();
        orderRepository.save(order);


        Payment payment = Payment.builder()
                .paymentAmount(toUse)
                .order(order)
                .build();
        paymentRepository.save(payment);
        return payment;
    }
}
