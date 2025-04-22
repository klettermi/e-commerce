package kr.hhplus.be.server.infrastructure.inventory;

import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryRepo;

    @Override
    public Optional<Inventory> findByProductId(Long id) {
        return inventoryRepo.findByProductId(id);
    }

    @Override
    public void save(Inventory inventory) {
        inventoryRepo.save(inventory);
    }

    @Override
    public void deleteAll() {
        inventoryRepo.deleteAll();
    }

    @Override
    public Optional<Inventory> findByProductIdForUpdate(Long id) {
        return inventoryRepo.findByProductIdForUpdate(id);
    }
}
