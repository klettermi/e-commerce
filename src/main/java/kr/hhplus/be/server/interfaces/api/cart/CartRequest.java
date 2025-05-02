package kr.hhplus.be.server.interfaces.api.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.cart.CartInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartRequest {

    @Getter @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Get {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        private Get(Long userId) {
            this.userId = userId;
        }

        public static Get of(Long userId) {
            return new Get(userId);
        }

        public CartInput.Get toInput() {
            CartInput.Get i = new CartInput.Get();
            i.setUserId(userId);
            return i;
        }
    }

    @Getter @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AddItem {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        @NotNull(message = "productId는 필수입니다.")
        private Long productId;

        @Positive(message = "quantity는 양수여야 합니다.")
        private Integer quantity;

        private AddItem(Long userId, Long productId, Integer quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public static AddItem of(Long userId, Long productId, Integer quantity) {
            return new AddItem(userId, productId, quantity);
        }

        public CartInput.AddItem toInput() {
            CartInput.AddItem i = new CartInput.AddItem();
            i.setUserId(userId);
            i.setProductId(productId);
            i.setQuantity(quantity);
            return i;
        }
    }

    @Getter @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateItem {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        @NotNull(message = "productId는 필수입니다.")
        private Long productId;

        @Positive(message = "quantity는 양수여야 합니다.")
        private Integer quantity;

        private UpdateItem(Long userId, Long productId, Integer quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public static UpdateItem of(Long userId, Long productId, Integer quantity) {
            return new UpdateItem(userId, productId, quantity);
        }

        public CartInput.UpdateItem toInput() {
            CartInput.UpdateItem i = new CartInput.UpdateItem();
            i.setUserId(userId);
            i.setProductId(productId);
            i.setQuantity(quantity);
            return i;
        }
    }

    @Getter @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RemoveItem {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        @NotNull(message = "productId는 필수입니다.")
        private Long productId;

        private RemoveItem(Long userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static RemoveItem of(Long userId, Long productId) {
            return new RemoveItem(userId, productId);
        }

        public CartInput.RemoveItem toInput() {
            CartInput.RemoveItem i = new CartInput.RemoveItem();
            i.setUserId(userId);
            i.setProductId(productId);
            return i;
        }
    }

    @Getter @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Clear {
        @NotNull(message = "userId는 필수입니다.")
        private Long userId;

        private Clear(Long userId) {
            this.userId = userId;
        }

        public static Clear of(Long userId) {
            return new Clear(userId);
        }

        public CartInput.Clear toInput() {
            CartInput.Clear i = new CartInput.Clear();
            i.setUserId(userId);
            return i;
        }
    }
}