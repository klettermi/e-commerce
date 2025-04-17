package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.interfaces.api.item.ItemRequest;
import kr.hhplus.be.server.interfaces.api.option.OptionRequest;
import kr.hhplus.be.server.interfaces.api.product.ProductResponse;
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
        ItemRequest itemRequest = new ItemRequest(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                new Money(BigDecimal.valueOf(10000)),
                LocalDateTime.now()
        );
        OptionRequest optionRequest = new OptionRequest(
                "Black",
                new Money(BigDecimal.valueOf(500))
        );

        Item item = Item.fromDto(itemRequest, category);
        Option option = Option.fromDto(optionRequest);
        Product product = new Product(item, option);

        // when
        Money finalPrice = product.calculateFinalPrice();

        // then
        assertEquals(0, new Money(BigDecimal.valueOf(10500)).compareTo(finalPrice), "상품의 가격은 10500 이어야 합니다.");
    }

    @Test
    void testToDtoConversion() {
        // given
        Category category = new Category();
        ItemRequest itemRequest = new ItemRequest(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                new Money(BigDecimal.valueOf(10000)),
                LocalDateTime.now()
        );
        OptionRequest optionRequest = new OptionRequest(
                "White240",
                new Money(BigDecimal.valueOf(500))
        );

        Item item = Item.fromDto(itemRequest, category);
        Option option = Option.fromDto(optionRequest);
        Product product = new Product(item, option);

        // when
        ProductResponse dto = product.toDto();

        // then
        assertNull(dto.id(), "Not persisted, so id should be null.");
        assertEquals("AirForce", dto.itemName(), "Item name should match.");
        assertEquals("White240", dto.optionName(), "Option name should match.");
        assertEquals(0, new Money(BigDecimal.valueOf(10500)).compareTo(dto.finalPrice()), "상품의 최종 가격은 10500이어야 합니다.");
    }
}