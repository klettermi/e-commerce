package kr.hhplus.be.server.application.point;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@NoArgsConstructor
public class PointInput {

    @Getter
    @NoArgsConstructor
    public static class Charge {
        @NotNull
        private Long userId;
        @NotNull
        @Min(1)
        private int amount;
    }

    @Getter
    @NoArgsConstructor
    public static class Use {
        @NotNull
        private Long userId;
        @NotNull
        @Min(1)
        private int amount;
    }

    @Getter
    @NoArgsConstructor
    public static class GetPoint {
        @NotNull
        private Long userId;
    }

    @Getter
    @NoArgsConstructor
    public static class GetHistory {
        @NotNull
        private Long userId;
    }
}
