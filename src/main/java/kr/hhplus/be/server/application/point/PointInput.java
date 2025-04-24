package kr.hhplus.be.server.application.point;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
public class PointInput {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Charge {
        @NotNull
        private Long userId;
        @NotNull
        @Min(1)
        private int amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Use {
        @NotNull
        private Long userId;
        @NotNull
        @Min(1)
        private int amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetPoint {
        @NotNull
        private Long userId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetHistory {
        @NotNull
        private Long userId;
    }
}
