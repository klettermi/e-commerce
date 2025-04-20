package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryChecker {

    private final InventoryRepository inventoryRepository;


    @Override
    @Transactional
    public boolean hasSufficientStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for productId: " + productId));
        return inventory.getQuantity() >= quantity;
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for productId: " + productId));
        inventory.decreaseStock(quantity);
        // 재고 감소 후 업데이트된 엔티티를 저장합니다.
        inventoryRepository.save(inventory);
    }
}
