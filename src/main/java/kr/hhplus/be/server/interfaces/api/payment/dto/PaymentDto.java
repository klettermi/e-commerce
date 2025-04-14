package kr.hhplus.be.server.interfaces.api.payment.dto;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.payment.Payment;

import java.math.BigDecimal;

public record PaymentDto(Long orderId, Money paidAmount) {
    public static PaymentDto from(Payment payment) {
        return new PaymentDto(
                payment.getOrder().getId(),
                payment.getPaymentAmount()
        );
    }
}