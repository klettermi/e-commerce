package kr.hhplus.be.server.interfaces.api.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.OrderInput;
import kr.hhplus.be.server.domain.common.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Place {
        @NotNull(message = "사용자 ID는 필수 입니다.")
        private Long userId;
        private Long couponId;

        @Valid
        @NotEmpty(message = "상품 목록은 1개 이상이어야 합니다.")
        private List<Item> items;

        public Place(Long userId, Long couponId, List<Item> items) {
            this.userId = userId;
            this.couponId = couponId;
            this.items = items;
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Item {
            @NotNull(message = "상품 ID는 필수 입니다.")
            @Positive(message = "상품 ID는 양수여야 합니다.")
            private Long productId;

            @Positive(message = "수량은 양수여야 합니다.")
            private int quantity;

            private Money unitPrice;

            public Item(Long productId, Money unitPrice, int quantity) {
                this.productId = productId;
                this.unitPrice = unitPrice;
                this.quantity = quantity;
            }
        }

        public OrderInput.Place toInput() {
            List<OrderInput.Item> inputItems = items.stream()
                    .map(i -> new OrderInput.Item(i.getProductId(), i.getUnitPrice(), i.getQuantity()))
                    .collect(Collectors.toList());
            return new OrderInput.Place(userId, inputItems);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Get {
        @NotNull(message = "주문 ID는 필수 입니다.")
        private Long orderId;

        public Get(Long orderId) {
            this.orderId = orderId;
        }

        public OrderInput.Get toInput() {
            return new OrderInput.Get(orderId);
        }
    }
}