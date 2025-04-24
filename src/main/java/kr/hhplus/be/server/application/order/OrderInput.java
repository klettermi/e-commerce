package kr.hhplus.be.server.application.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class OrderInput {
    @Getter
    public static class Get {
        private Long orderId;
    }

    @Getter
    public static class Place {
        private Long userId;
        private List<Item> items;
    }

    @Getter
    public static class Item {
        private Long productId;
        private int quantity;
    }
}