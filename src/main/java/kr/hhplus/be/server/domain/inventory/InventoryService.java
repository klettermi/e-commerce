package kr.hhplus.be.server.domain.inventory;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * 재고 검증 후 차감
     */
    @Transactional
    public InventoryInfo.StockCheckResult checkAndDecreaseStock(InventoryCommand.DecreaseStock command) {
        List<OrderProduct> products = command.getOrderProducts();

        var updatedItems = products.stream().map(p -> {
            Inventory inv = inventoryRepository
                    .findByProductIdForUpdate(p.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Inventory not found: productId=" + p.getProductId()
                    ));

            if (inv.getQuantity() < p.getQuantity()) {
                throw new InvalidStateException(
                        "재고 부족: productId=" + p.getProductId()
                );
            }
            inv.decreaseStock(p.getQuantity());
            // no explicit save required here; within @Transactional, changes flush automatically
            return InventoryInfo.InventoryItem.builder()
                    .productId(p.getProductId())
                    .remainingQuantity(inv.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        return InventoryInfo.StockCheckResult.builder()
                .success(true)
                .inventories(updatedItems)
                .build();
    }
}
