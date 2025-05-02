package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PaymentInfo {
    @Getter
    @Builder
    public static class PaymentResult {
        private Long id;
        private Long orderId;
        private Money paymentAmount;
    }
}