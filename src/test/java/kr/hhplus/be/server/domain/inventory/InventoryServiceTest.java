package kr.hhplus.be.server.domain.inventory;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.common.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void checkAndDecreaseStock_success() {
        OrderProduct p1 = OrderProduct.builder()
                .productId(1L).quantity(5)
                .unitPoint(new Money(BigDecimal.ZERO))
                .build();
        OrderProduct p2 = OrderProduct.builder()
                .productId(2L).quantity(3)
                .unitPoint(new Money(BigDecimal.ZERO))
                .build();

        Inventory inv1 = mock(Inventory.class);
        when(inv1.getQuantity()).thenReturn(10);
        Inventory inv2 = mock(Inventory.class);
        when(inv2.getQuantity()).thenReturn(5);

        when(inventoryRepository.findByProductIdForUpdate(1L))
                .thenReturn(Optional.of(inv1));
        when(inventoryRepository.findByProductIdForUpdate(2L))
                .thenReturn(Optional.of(inv2));

        inventoryService.checkAndDecreaseStock(List.of(p1, p2));

        verify(inv1).decreaseStock(5);
        verify(inv2).decreaseStock(3);
    }

    @Test
    void checkAndDecreaseStock_inventoryNotFound_throwsEntityNotFound() {
        OrderProduct p = OrderProduct.builder()
                .productId(99L).quantity(1)
                .unitPoint(new Money(BigDecimal.ZERO))
                .build();

        when(inventoryRepository.findByProductIdForUpdate(99L))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> inventoryService.checkAndDecreaseStock(List.of(p))
        );
        assertTrue(ex.getMessage().contains("Inventory not found"));

        // inventoryRepository 외에 다른 mock 과의 상호작용이 없어야 합니다.
        verify(inventoryRepository).findByProductIdForUpdate(99L);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    void checkAndDecreaseStock_insufficientStock_throwsInvalidState() {
        OrderProduct p = OrderProduct.builder()
                .productId(5L).quantity(7)
                .unitPoint(new Money(BigDecimal.ZERO))
                .build();

        Inventory inv = mock(Inventory.class);
        when(inv.getQuantity()).thenReturn(5);
        when(inventoryRepository.findByProductIdForUpdate(5L))
                .thenReturn(Optional.of(inv));

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> inventoryService.checkAndDecreaseStock(List.of(p))
        );
        assertTrue(ex.getMessage().contains("재고 부족"));

        // 재고 부족 예외 시 decreaseStock 은 호출되지 않아야 합니다.
        verify(inv, never()).decreaseStock(anyInt());
    }
}
