package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void hasSufficientStock_whenStockIsSufficient_returnsTrue() {
        // given
        Long productId = 1L;
        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inv));

        // when
        boolean ok = inventoryService.hasSufficientStock(productId, 5);

        // then
        assertThat(ok).isTrue();
    }

    @Test
    void hasSufficientStock_whenStockIsInsufficient_returnsFalse() {
        // given
        Long productId = 1L;
        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(3)
                .build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inv));

        // when
        boolean ok = inventoryService.hasSufficientStock(productId, 5);

        // then
        assertThat(ok).isFalse();
    }

    @Test
    void hasSufficientStock_whenInventoryNotFound_throwsException() {
        when(inventoryRepository.findByProductId(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.hasSufficientStock(1L, 5))
                .isInstanceOf(DomainExceptions.EntityNotFoundException.class)
                .hasMessageContaining("Inventory not found");
    }

    @Test
    void decreaseStock_success_updatesAndSaves() {
        // given
        Long productId = 1L;
        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inv));

        // when
        inventoryService.decreaseStock(productId, 3);

        // then
        assertThat(inv.getQuantity()).isEqualTo(7);
        verify(inventoryRepository).save(inv);
    }

    @Test
    void decreaseStock_whenInventoryNotFound_throwsException() {
        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.decreaseStock(1L, 3))
                .isInstanceOf(DomainExceptions.EntityNotFoundException.class);
    }

    @Test
    void decreaseStock_whenInsufficientStock_throwsException() {
        // given
        Long productId = 1L;
        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(2)
                .build();
        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inv));

        // when / then
        assertThatThrownBy(() ->
                inventoryService.decreaseStock(productId, 3))
                .isInstanceOf(DomainExceptions.InvalidStateException.class)
                .hasMessageContaining("재고 부족: productId="+productId);
    }
}
