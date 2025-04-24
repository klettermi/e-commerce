package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.point.PointInput;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointRequest {

    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Charge {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        @NotNull(message = "amount는 필수입니다.")
        @Min(value = 1, message = "amount는 1 이상이어야 합니다.")
        private Integer amount;

        public PointInput.Charge toInput() {
            PointInput.Charge i = new PointInput.Charge();
            i.setUserId(userId);
            i.setAmount(amount);
            return i;
        }
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Use {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        @NotNull(message = "amount는 필수입니다.")
        @Min(value = 1, message = "amount는 1 이상이어야 합니다.")
        private Integer amount;

        public PointInput.Use toInput() {
            PointInput.Use i = new PointInput.Use();
            i.setUserId(userId);
            i.setAmount(amount);
            return i;
        }
    }
}