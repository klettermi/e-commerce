package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.interfaces.api.item.ItemRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemTest {

    @Test
    void fromDto_createsItemSuccessfully() {
        String name = "Test Item";
        String description = "This is a test item description";
        SaleStatus saleStatus = SaleStatus.ON_SALE;
        Money basePrice = Money.of(1000);
        LocalDateTime saleStartDate = LocalDateTime.now();

        ItemRequest dto = new ItemRequest(name, description, saleStatus, basePrice, saleStartDate);

        Category category = new Category();

        Item item = Item.fromDto(dto, category);

        assertNotNull(item, "생성된 Item 객체는 null이 아니어야 합니다.");
        assertEquals(name, item.getName(), "이름 필드가 DTO와 동일해야 합니다.");
        assertEquals(description, item.getDescription(), "설명 필드가 DTO와 동일해야 합니다.");
        assertEquals(saleStatus, item.getSaleStatus(), "판매 상태가 DTO와 동일해야 합니다.");
        assertEquals(basePrice, item.getBasePrice(), "기본 가격이 DTO와 동일해야 합니다.");
        assertEquals(saleStartDate, item.getSaleStartDate(), "판매 시작 날짜가 DTO와 동일해야 합니다.");
        assertEquals(category, item.getCategory(), "카테고리 객체가 올바르게 설정되어야 합니다.");
    }
}
