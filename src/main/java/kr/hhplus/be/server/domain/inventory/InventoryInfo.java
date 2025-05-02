package kr.hhplus.be.server.domain.inventory;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class InventoryInfo {

    @Getter
    @Builder
    public static class StockCheckResult {
        private final boolean success;
        private final List<InventoryItem> inventories;
    }

    @Getter
    @Builder
    public static class InventoryItem {
        private final Long productId;
        private final int remainingQuantity;
    }
}