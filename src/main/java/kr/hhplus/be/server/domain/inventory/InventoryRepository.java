package kr.hhplus.be.server.domain.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Collection<InventoryRepository> findByProductId(Long id);
}
