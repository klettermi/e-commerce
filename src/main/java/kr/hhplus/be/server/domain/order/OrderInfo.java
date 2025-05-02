package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderInfo {

    @Getter
    @Builder
    public static class Total {
        private final Money totalPoint;
    }

    @Getter
    @Builder
    public static class OrderDetail {
        private final Long orderId;
        private final Long userId;
        private final Money totalPoint;
        private final String status;
        private final List<OrderProductInfo> items;
    }

    @Getter
    @Builder
    public static class OrderProductInfo {
        private final Long productId;
        private final int quantity;
        private final Money unitPoint;
    }
}
