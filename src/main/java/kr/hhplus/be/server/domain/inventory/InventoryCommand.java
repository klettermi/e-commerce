package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryCommand {

    @Getter
    public static class DecreaseStock {
        private final List<OrderProduct> orderProducts;

        private DecreaseStock(List<OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
        }

        public static DecreaseStock of(List<OrderProduct> orderProducts) {
            return new DecreaseStock(orderProducts);
        }
    }
}
