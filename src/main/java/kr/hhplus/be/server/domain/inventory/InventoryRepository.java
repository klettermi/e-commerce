package kr.hhplus.be.server.domain.inventory;

import java.util.Optional;

public interface InventoryRepository {
    Optional<Inventory> findByProductId(Long id);

    void save(Inventory inventory);
}
