package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    public static class CalculateTotal {
        private final List<OrderProduct> items;

        private CalculateTotal(List<OrderProduct> items) {
            this.items = items;
        }

        public static CalculateTotal of(List<OrderProduct> items) {
            return new CalculateTotal(items);
        }
    }

    @Getter
    public static class BuildOrder {
        private final Long userId;
        private final List<OrderProduct> items;
        private final Money totalPoint;

        private BuildOrder(Long userId, List<OrderProduct> items, Money totalPoint) {
            this.userId = userId;
            this.items = items;
            this.totalPoint = totalPoint;
        }

        public static BuildOrder of(Long userId, List<OrderProduct> items, Money totalPoint) {
            return new BuildOrder(userId, items, totalPoint);
        }
    }

    @Getter
    public static class SaveOrder {
        private final Order order;

        private SaveOrder(Order order) {
            this.order = order;
        }

        public static SaveOrder of(Order order) {
            return new SaveOrder(order);
        }
    }

    @Getter
    public static class GetOrder {
        private final Long orderId;

        private GetOrder(Long orderId) {
            this.orderId = orderId;
        }

        public static GetOrder of(Long orderId) {
            return new GetOrder(orderId);
        }
    }

    @Getter
    public static class MarkPaid {
        private final Long orderId;

        private MarkPaid(Long orderId) {
            this.orderId = orderId;
        }

        public static MarkPaid of(Long orderId) {
            return new MarkPaid(orderId);
        }
    }
}