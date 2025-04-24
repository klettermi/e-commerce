package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointCommand {

    @Getter
    public static class GetPoint {
        private final Long userId;

        private GetPoint(Long userId) {
            this.userId = userId;
        }

        public static GetPoint of(Long userId) {
            return new GetPoint(userId);
        }
    }

    @Getter
    public static class GetHistory {
        private final Long userId;

        private GetHistory(Long userId) {
            this.userId = userId;
        }

        public static GetHistory of(Long userId) {
            return new GetHistory(userId);
        }
    }

    @Getter
    public static class Charge {
        private final Long userId;
        private final Money amount;

        private Charge(Long userId, Money amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, Money amount) {
            return new Charge(userId, amount);
        }
    }

    @Getter
    public static class Use {
        private final Long userId;
        private final Money amount;

        private Use(Long userId, Money amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Use of(Long userId, Money amount) {
            return new Use(userId, amount);
        }
    }
}

