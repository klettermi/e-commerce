package kr.hhplus.be.server.application.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouponInput {

    @Getter
    @Builder
    public static class Issue {
        private Long couponId;
        private Long userId;

        public static Issue of(@NotNull(message = "couponId는 필수입니다.") Long couponId, @NotNull(message = "userId는 필수입니다.") Long userId) {
            return new Issue(couponId, userId);
        }
    }

    @Getter
    @Builder
    public static class GetByUser {
        private Long userId;

        public static GetByUser of(@NotNull(message = "userId는 필수입니다.") Long userId) {
            return GetByUser.builder().userId(userId).build();
        }
    }
}

