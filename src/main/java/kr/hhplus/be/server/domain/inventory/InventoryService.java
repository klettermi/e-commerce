package kr.hhplus.be.server.domain.inventory;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.application.redis.RedissonLockService;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final RedissonLockService lockService;
    private final InventoryRepository inventoryRepository;

    /**
     * 재고 검증 후 차감
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndDecreaseStock(InventoryCommand.DecreaseStock command) {
        List<OrderProduct> products = command.getOrderProducts();

        // 1) 락 키 생성 (productId 기준), 정렬해서 교착 방지
        List<String> lockKeys = products.stream()
                .map(p -> "stock:" + p.getProductId())
                .sorted()
                .toList();

        // 2) 성공적으로 획득한 키만 담을 리스트
        List<String> acquired = new ArrayList<>();

        try {
            // 3) 락 획득 시도
            for (String key : lockKeys) {
                boolean ok = lockService.tryLock(key, 5_000);
                if (!ok) {
                    throw new IllegalStateException("락 획득 실패: " + key);
                }
                acquired.add(key);
            }

            // 4) 기존 재고 차감 로직
            for (OrderProduct p : products) {
                Inventory inv = inventoryRepository
                        .findByProductIdForUpdate(p.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Inventory not found: productId=" + p.getProductId()
                        ));

                if (inv.getQuantity() < p.getQuantity()) {
                    throw new InvalidStateException("재고 부족: productId=" + p.getProductId());
                }
                inv.decreaseStock(p.getQuantity());
            }
        } finally {
            // 5) reverse 순서로, 각각 안전하게 해제
            Collections.reverse(acquired);
            for (String key : acquired) {
                try {
                    lockService.unlock(key);
                } catch (Exception e) {
                    log.warn("락 해제 실패 [{}]: {}", key, e.getMessage());
                }
            }
        }
    }
}
