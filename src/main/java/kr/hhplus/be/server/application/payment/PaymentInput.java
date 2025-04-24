package kr.hhplus.be.server.application.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class PaymentInput {

    @Getter
    public static class Process {
        private Long orderId;
        private Long userId;
        private Long couponId;
    }
}