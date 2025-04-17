package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.infrastructure.point.UserPointJpaRepository;
import kr.hhplus.be.server.interfaces.api.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderJpaRepository orderJpaRepository;
    private final UserPointJpaRepository userPointJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    @Transactional
    public Payment processPayment(Long orderId, Long userId, Long couponId) throws InvalidStateException {
        Order order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        UserPoint userPoint = userPointJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for user id: " + userId));

        Money requiredPoints = order.getTotalPoint();
        if (userPoint.getPointBalance().compareTo(requiredPoints) < 0) {
            throw new InvalidStateException("Insufficient points: required " + requiredPoints +
                    ", available " + userPoint.getPointBalance());
        }

        userPoint.usePoints(requiredPoints);
        userPointJpaRepository.save(userPoint);

        order.markAsPaid();
        orderJpaRepository.save(order);

        PaymentResponse paymentResponse = new PaymentResponse(
                orderId,
                requiredPoints

        );

        Payment payment = Payment.toEntity(paymentResponse, order);
        paymentJpaRepository.save(payment);
        return payment;
    }
}
