package kr.hhplus.be.server.infrastructure.inventory;

import kr.hhplus.be.server.domain.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductId(Long id);
}
