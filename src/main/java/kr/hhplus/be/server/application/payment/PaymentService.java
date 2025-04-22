package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
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

    @Transactional
    public Payment processPayment(Long orderId, Long userId, Long couponId) throws InvalidStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        UserPoint userPoint = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for user id: " + userId));

        Money requiredPoints = order.getTotalPoint();
        if (userPoint.getPointBalance().compareTo(requiredPoints) < 0) {
            throw new InvalidStateException("Insufficient points: required " + requiredPoints +
                    ", available " + userPoint.getPointBalance());
        }

        userPoint.usePoints(requiredPoints);
        pointRepository.save(userPoint);

        order.markAsPaid();
        orderRepository.save(order);


        Payment payment = Payment.builder()
                .paymentAmount(requiredPoints)
                .order(order)
                .build();
        paymentRepository.save(payment);
        return payment;
    }
}
