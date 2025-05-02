package kr.hhplus.be.server.application.order;


import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderOutput {
    private Long orderId;
    private Long userId;
    private Money totalPoint;
    private String status;
    private List<Item> items;

    @Getter
    @Builder
    public static class Item {
        private Long productId;
        private int quantity;
        private Money unitPoint;
    }
}