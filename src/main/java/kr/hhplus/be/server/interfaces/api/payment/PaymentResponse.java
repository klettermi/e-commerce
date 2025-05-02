package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.application.payment.PaymentOutput;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private long paymentAmount;

    public static PaymentResponse fromOutput(PaymentOutput.Result output) {
        return PaymentResponse.builder()
                .id(output.getId())
                .orderId(output.getOrderId())
                .paymentAmount(output.getPaymentAmount())
                .build();
    }
}