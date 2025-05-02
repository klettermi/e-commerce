package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;

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
