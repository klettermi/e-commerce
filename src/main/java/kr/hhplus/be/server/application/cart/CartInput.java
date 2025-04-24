package kr.hhplus.be.server.application.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CartInput {

    @Getter
    @Setter
    public static class Get {
        private Long userId;
    }

    @Getter
    @Setter
    public static class AddItem {
        private Long userId;
        private Long productId;
        private int quantity;
    }

    @Getter
    @Setter
    public static class UpdateItem {
        private Long userId;
        private Long productId;
        private int quantity;
    }

    @Getter
    @Setter
    public static class RemoveItem {
        private Long userId;
        private Long productId;
    }

    @Getter
    @Setter
    public static class Clear {
        private Long userId;
    }
}