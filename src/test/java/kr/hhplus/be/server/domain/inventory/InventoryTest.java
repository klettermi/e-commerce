package kr.hhplus.be.server.domain.inventory;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;
import static org.junit.jupiter.api.Assertions.*;


public class InventoryTest {

    @Test
    void 재고차감_정상처리() {
        // given: 초기 재고 10개
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        // when: 5개 차감
        inventory.decreaseStock(5);

        // then: 남은 재고 5개
        assertEquals(5, inventory.getQuantity(), "재고 차감 후 남은 수량이 5여야 합니다.");
    }

    @Test
    void 재고차감_재고부족_예외발생() {
        // given: 초기 재고 3개
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(3)
                .build();

        // when, then: 5개 차감 시 재고 부족 예외 발생
        Exception exception = assertThrows(InvalidStateException.class, () -> {
            inventory.decreaseStock(5);
        });
        assertTrue(exception.getMessage().contains("재고 부족"), "재고 부족 메시지가 포함되어야 합니다.");
    }

    @Test
    void 재고차감_음수입력_예외발생() {
        // given: 초기 재고 10개
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        // when, then: 음수 값 입력 시 IllegalArgumentException 발생
        Exception exception = assertThrows(InvalidStateException.class, () -> {
            inventory.decreaseStock(-1);
        });
        assertTrue(exception.getMessage().contains("감소 수량은 음수가 될 수 없습니다"),
                "음수 입력에 대한 예외 메시지가 포함되어야 합니다.");
    }

    @Test
    void 재고증가_정상처리() {
        // given: 초기 재고 10개
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        // when: 5개 증가
        inventory.increaseStock(5);

        // then: 총 재고는 15개
        assertEquals(15, inventory.getQuantity(), "재고 증가 후 총 수량은 15여야 합니다.");
    }

    @Test
    void 재고증가_음수입력_예외발생() {
        // given: 초기 재고 10개
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        Exception exception = assertThrows(InvalidStateException.class, () -> {
            inventory.increaseStock(-3);
        });
        assertTrue(exception.getMessage().contains("증가 수량은 음수가 될 수 없습니다"),
                "음수 입력에 대한 예외 메시지가 포함되어야 합니다.");
    }
}