package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
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
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;  // 추가 주입

    public PaymentInfo.PaymentResult savePayment(PaymentCommand.SavePayment cmd) {
        // 주문 조회
        Order order = orderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + cmd.getOrderId()));

        // 결제 엔티티 생성/저장
        Payment payment = Payment.builder()
                .paymentAmount(cmd.getPaymentAmount())
                .order(order)
                .build();
        Payment saved = paymentRepository.save(payment);

        // 결과 매핑
        return PaymentInfo.PaymentResult.builder()
                .id(saved.getId())
                .orderId(order.getId())
                .paymentAmount(saved.getPaymentAmount())
                .build();
    }
}
