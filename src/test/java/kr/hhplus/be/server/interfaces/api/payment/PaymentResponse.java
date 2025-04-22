package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.payment.Payment;

public record PaymentResponse(Long orderId, Money paidAmount) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getOrder().getId(),
                payment.getPaymentAmount()
        );
    }
}