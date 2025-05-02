package kr.hhplus.be.server.application.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentOutput {

    @Getter
    @Builder
    public static class Result {
        private Long id;
        private Long orderId;
        private long paymentAmount;
    }
}
