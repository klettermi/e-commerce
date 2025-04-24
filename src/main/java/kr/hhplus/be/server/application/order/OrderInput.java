package kr.hhplus.be.server.application.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
public class OrderInput {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Long orderId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Place {
        private Long userId;
        private List<Item> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long productId;
        private int quantity;
    }
}