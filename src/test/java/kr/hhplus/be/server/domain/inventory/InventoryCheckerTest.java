package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.infrastructure.inventory.InventoryJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class InventoryCheckerTest {

    @Mock
    private InventoryJpaRepository inventoryJpaRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHasSufficientStock_whenStockIsSufficient() {
        // given
        Long productId = 1L;
        int requiredQuantity = 5;
        // Inventory 엔티티 생성 (예: 초기 재고 10)
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .build();
        when(inventoryJpaRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        // when
        boolean result = inventoryService.hasSufficientStock(productId, requiredQuantity);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testHasSufficientStock_whenStockIsInsufficient() {
        // given
        Long productId = 1L;
        int requiredQuantity = 5;
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(3)
                .build();
        when(inventoryJpaRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        // when
        boolean result = inventoryService.hasSufficientStock(productId, requiredQuantity);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void testHasSufficientStock_whenInventoryNotFound() {
        // given
        Long productId = 1L;
        int requiredQuantity = 5;
        when(inventoryJpaRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(DomainExceptions.EntityNotFoundException.class, () ->
                inventoryService.hasSufficientStock(productId, requiredQuantity));
    }

    @Test
    void testDecreaseStock_success() {
        // given
        Long productId = 1L;
        int initialQuantity = 10;
        int decreaseQuantity = 3;
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(initialQuantity)
                .build();
        when(inventoryJpaRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        // when
        inventoryService.decreaseStock(productId, decreaseQuantity);

        // then
        // Inventory 엔티티의 decreaseStock 메서드가 정상적으로 차감하여 재고가 initialQuantity - decreaseQuantity가 되어야 함
        assertEquals(initialQuantity - decreaseQuantity, inventory.getQuantity());
        // 재고 변경 후 save가 호출되어야 함
        verify(inventoryJpaRepository, times(1)).save(inventory);
    }

    @Test
    void testDecreaseStock_whenInventoryNotFound() {
        // given
        Long productId = 1L;
        int decreaseQuantity = 3;
        when(inventoryJpaRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DomainExceptions.EntityNotFoundException.class, () ->
                inventoryService.decreaseStock(productId, decreaseQuantity));
    }

    @Test
    void testDecreaseStock_whenInsufficientStock_thenThrowsException() {
        // given
        Long productId = 1L;
        int initialQuantity = 2;
        int decreaseQuantity = 3;
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(initialQuantity)
                .build();
        when(inventoryJpaRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        // when & then
        // Inventory 엔티티의 decreaseStock 메서드 내에서 재고 부족 시 DomainExceptions.InvalidStateException을 던지도록 구현되었다고 가정
        assertThrows(DomainExceptions.InvalidStateException.class, () ->
                inventoryService.decreaseStock(productId, decreaseQuantity));
    }
}