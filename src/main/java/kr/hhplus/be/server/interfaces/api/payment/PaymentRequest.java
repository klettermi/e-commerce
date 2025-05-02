package kr.hhplus.be.server.interfaces.api.payment;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.payment.PaymentInput;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequest {

    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Process {
        @NotNull(message = "orderId는 필수입니다.")
        private Long orderId;

        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        private Long couponId;

        public PaymentInput.Process toInput() {
            PaymentInput.Process i = new PaymentInput.Process();
            i.setOrderId(orderId);
            i.setUserId(userId);
            i.setCouponId(couponId);
            return i;
        }
    }
}
