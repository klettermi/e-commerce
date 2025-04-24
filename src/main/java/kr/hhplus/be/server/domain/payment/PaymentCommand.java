package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {
    @Getter
    public static class SavePayment {
        private final Long orderId;
        private final Money paymentAmount;

        private SavePayment(Long orderId, Money paymentAmount) {
            this.orderId = orderId;
            this.paymentAmount = paymentAmount;
        }

        public static SavePayment of(Long orderId, Money paymentAmount) {
            return new SavePayment(orderId, paymentAmount);
        }
    }
}
