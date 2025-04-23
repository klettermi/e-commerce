package kr.hhplus.be.server.domain.inventory;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * 재고 검증 후 차감
     */
    public void checkAndDecreaseStock(List<OrderProduct> orderProducts) {
        for (OrderProduct p : orderProducts) {
            Inventory inv = inventoryRepository
                    .findByProductIdForUpdate(p.getProductId())  // @Lock(PESSIMISTIC_WRITE)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Inventory not found: productId=" + p.getProductId()
                    ));

            if (inv.getQuantity() < p.getQuantity()) {
                throw new InvalidStateException(
                        "재고 부족: productId=" + p.getProductId()
                );
            }
            inv.decreaseStock(p.getQuantity());
        }
    }
}
