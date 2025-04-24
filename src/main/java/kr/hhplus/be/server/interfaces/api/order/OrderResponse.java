package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.application.order.OrderOutput;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPoint;
    private String status;
    private List<Item> items;

    @Getter
    @Builder
    public static class Item {
        private Long productId;
        private int quantity;
        private BigDecimal unitPoint;
    }

    public static OrderResponse fromOutput(OrderOutput output) {
        List<Item> itemList = output.getItems().stream()
                .map(i -> Item.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .unitPoint(i.getUnitPoint().amount())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(output.getOrderId())
                .userId(output.getUserId())
                .totalPoint(output.getTotalPoint().amount())
                .status(output.getStatus())
                .items(itemList)
                .build();
    }
}
