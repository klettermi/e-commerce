package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.point.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserPointRepository userPointRepository;

    @Transactional
    public Order processPayment(Long orderId, Long userId, Long couponId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserPoint not found for user id: " + userId));

        int requiredPoints = order.getTotalPoint().amount().intValue();
        if (userPoint.getPointBalance() < requiredPoints) {
            throw new IllegalArgumentException("Insufficient points: required " + requiredPoints +
                    ", available " + userPoint.getPointBalance());
        }

        userPoint.usePoints(requiredPoints);
        userPointRepository.save(userPoint);

        order.markAsPaid();
        order.getOrderProducts().size();
        return orderRepository.save(order);
    }
}
