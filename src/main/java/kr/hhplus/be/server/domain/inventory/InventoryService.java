package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.infrastructure.inventory.InventoryJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InventoryChecker {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryService(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public boolean hasSufficientStock(Long productId, int quantity) {
        Inventory inventory = inventoryJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new DomainExceptions.EntityNotFoundException("Inventory not found for productId: " + productId));
        return inventory.getQuantity() >= quantity;
    }

    @Override
    public void decreaseStock(Long productId, int quantity) {
        Inventory inventory = inventoryJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new DomainExceptions.EntityNotFoundException("Inventory not found for productId: " + productId));
        inventory.decreaseStock(quantity);
        // 재고 감소 후 업데이트된 엔티티를 저장합니다.
        inventoryJpaRepository.save(inventory);
    }
}
