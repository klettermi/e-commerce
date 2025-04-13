package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.api.item.dto.ItemDto;
import kr.hhplus.be.server.interfaces.api.option.dto.OptionDto;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testCalculateFinalPriceWithoutDiscount() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                BigDecimal.valueOf(10000),
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "Black",
                BigDecimal.valueOf(5000)
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromDto(optionDto);
        Product product = new Product(item, option);

        // when
        BigDecimal finalPrice = product.calculateFinalPrice();

        // then
        assertEquals(0, BigDecimal.valueOf(10500).compareTo(finalPrice), "상품의 가격은 10500 이어야 합니다.");
    }

    @Test
    void testToDtoConversion() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                BigDecimal.valueOf(10000),
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "White240",
                BigDecimal.valueOf(5000)
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromDto(optionDto);
        Product product = new Product(item, option);

        // when
        ProductDto dto = product.toDto();

        // then
        assertNull(dto.id(), "Not persisted, so id should be null.");
        assertEquals("AirForce", dto.itemName(), "Item name should match.");
        assertEquals("White240", dto.optionName(), "Option name should match.");
        assertEquals(0, BigDecimal.valueOf(10500).compareTo(dto.finalPrice()), "두 번째 상품의 최종 가격은 144000이어야 합니다.");
    }
}