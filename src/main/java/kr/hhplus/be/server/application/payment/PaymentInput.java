package kr.hhplus.be.server.application.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
public class PaymentInput {

    @Getter
    @Setter
    public static class Process {
        private Long orderId;
        private Long userId;
        private Long couponId;
    }
}