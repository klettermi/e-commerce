package kr.hhplus.be.server.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import org.springframework.stereotype.Component;

@Component
public class JpaInventoryChecker implements InventoryChecker {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean hasSufficientStock(Long productId, int quantity) {
        Inventory inventory = em.createQuery(
                        "SELECT i FROM Inventory i WHERE i.productId = :productId", Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();
        return inventory.getQuantity() >= quantity;
    }

    @Override
    public void decreaseStock(Long productId, int quantity) {
        Inventory inventory = em.createQuery(
                        "SELECT i FROM Inventory i WHERE i.productId = :productId", Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();
        inventory.decreaseStock(quantity);
    }
}