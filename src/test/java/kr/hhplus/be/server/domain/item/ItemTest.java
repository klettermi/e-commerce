package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.interfaces.api.item.dto.ItemDto;
import kr.hhplus.be.server.domain.item.SaleStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void fromDto_정상적으로_Item_생성() {
        // given: 테스트 데이터를 준비합니다.
        String name = "Test Item";
        String description = "This is a test item description";
        SaleStatus saleStatus = SaleStatus.ON_SALE;
        int basePrice = 1000;
        LocalDateTime saleStartDate = LocalDateTime.now();

        // ItemDto가 record 형태라고 가정 (또는 일반 DTO)
        ItemDto dto = new ItemDto(name, description, saleStatus, basePrice, saleStartDate);

        // Category 엔티티를 준비합니다.
        Category category = new Category();

        // when: fromDto를 호출하여 Item 엔티티를 생성합니다.
        Item item = Item.fromDto(dto, category);

        // then: 각 필드가 올바르게 매핑되었는지 검증합니다.
        assertNotNull(item, "생성된 Item 객체는 null이 아니어야 합니다.");
        assertEquals(name, item.getName(), "이름 필드가 DTO와 동일해야 합니다.");
        assertEquals(description, item.getDescription(), "설명 필드가 DTO와 동일해야 합니다.");
        assertEquals(saleStatus, item.getSaleStatus(), "판매 상태가 DTO와 동일해야 합니다.");
        assertEquals(basePrice, item.getBasePrice(), "기본 가격이 DTO와 동일해야 합니다.");
        assertEquals(saleStartDate, item.getSaleStartDate(), "판매 시작 날짜가 DTO와 동일해야 합니다.");
        assertEquals(category, item.getCategory(), "카테고리 객체가 올바르게 설정되어야 합니다.");
    }
}
