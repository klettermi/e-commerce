package kr.hhplus.be.server.infrastructure.inventory;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.productId = :id")
    Optional<Inventory> findByProductIdForUpdate(Long id);
}
