package kr.hhplus.be.server.domain.inventory;

public interface InventoryChecker {

    /**
     * 제품(productId)에 대해 quantity만큼 재고가 충분한지 여부를 반환
     */
    boolean hasSufficientStock(Long productId, int quantity);

    /**
     * 제품(productId)에 대해 quantity만큼 재고 차감
     */
    void decreaseStock(Long productId, int quantity);

}
