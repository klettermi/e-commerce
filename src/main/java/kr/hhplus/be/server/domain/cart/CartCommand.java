package kr.hhplus.be.server.domain.cart;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Command objects for Cart operations
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartCommand {

    @Getter
    public static class GetCart {
        private final Long userId;

        private GetCart(Long userId) {
            this.userId = userId;
        }

        public static GetCart of(Long userId) {
            return new GetCart(userId);
        }
    }

    @Getter
    public static class AddItem {
        private final Long userId;
        private final Long productId;
        private final int quantity;

        private AddItem(Long userId, Long productId, int quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public static AddItem of(Long userId, Long productId, int quantity) {
            return new AddItem(userId, productId, quantity);
        }
    }

    @Getter
    public static class UpdateItem {
        private final Long userId;
        private final Long productId;
        private final int quantity;

        private UpdateItem(Long userId, Long productId, int quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public static UpdateItem of(Long userId, Long productId, int quantity) {
            return new UpdateItem(userId, productId, quantity);
        }
    }

    @Getter
    public static class RemoveItem {
        private final Long userId;
        private final Long productId;

        private RemoveItem(Long userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static RemoveItem of(Long userId, Long productId) {
            return new RemoveItem(userId, productId);
        }
    }

    @Getter
    public static class ClearCart {
        private final Long userId;

        private ClearCart(Long userId) {
            this.userId = userId;
        }

        public static ClearCart of(Long userId) {
            return new ClearCart(userId);
        }
    }
}

