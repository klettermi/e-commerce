package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.point.UserPointRepository;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final UserPointRepository userPointRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Long orderId, Long userId, Long couponId) throws InvalidStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPoint not found for user id: " + userId));

        BigDecimal requiredPoints = order.getTotalPoint().amount();
        if (userPoint.getPointBalance().compareTo(requiredPoints) < 0) {
            throw new InvalidStateException("Insufficient points: required " + requiredPoints +
                    ", available " + userPoint.getPointBalance());
        }

        userPoint.usePoints(requiredPoints);
        userPointRepository.save(userPoint);

        order.markAsPaid();
        orderRepository.save(order);

        PaymentDto paymentDto = new PaymentDto(
                orderId,
                requiredPoints

        );

        Payment payment = Payment.toEntity(paymentDto, order);
        paymentRepository.save(payment);
        return payment;
    }
}
