package kr.hhplus.be.server.interfaces.api.coupon;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.coupon.CouponInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Issue {
        @NotNull(message = "couponId는 필수입니다.")
        private Long couponId;

        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        private Issue(Long couponId, Long userId) {
            this.couponId = couponId;
            this.userId = userId;
        }

        public static Issue of(Long couponId, Long userId) {
            return new Issue(couponId, userId);
        }

        public CouponInput.Issue toInput() {
            return CouponInput.Issue.of(couponId, userId);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListByUser {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        private ListByUser(Long userId) {
            this.userId = userId;
        }

        public static ListByUser of(Long userId) {
            return new ListByUser(userId);
        }

        public CouponInput.GetByUser toInput() {
            return CouponInput.GetByUser.of(userId);
        }
    }
}