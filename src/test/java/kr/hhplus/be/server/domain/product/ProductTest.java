package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.api.item.dto.ItemDto;
import kr.hhplus.be.server.interfaces.api.option.dto.OptionDto;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductTest {
    @Test
    void testCalculateFinalPriceWithoutDiscount() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                100000,
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "Black",
                5000
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromOption(optionDto);
        Product product = new Product(item, option);

        // when
        double finalPrice = product.calculateFinalPrice();

        // then
        assertEquals(105000.0, finalPrice, 0.001, "Final price without discount should be 105000.0");
    }

    @Test
    void testCalculateFinalPriceWithFixedDiscount() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                100000,
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "Black",
                5000
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromOption(optionDto);
        Product product = new Product(item, option);
        Product discountedProduct = product.applyFixedDiscount(5000.0);

        // when
        double finalPrice = discountedProduct.calculateFinalPrice();

        // then
        assertEquals(100000.0, finalPrice, 0.001, "Final price with fixed discount should be 100000.0");
    }

    @Test
    void testCalculateFinalPriceWithPercentDiscount() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                150000,
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "Black",
                10000
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromOption(optionDto);
        Product product = new Product(item, option);
        Product discountedProduct = product.applyPercentDiscount(0.10);

        // when
        double finalPrice = discountedProduct.calculateFinalPrice();

        // then
        assertEquals(144000.0, finalPrice, 0.001, "Final price with percent discount should be 144000.0");
    }

    @Test
    void testToDtoConversion() {
        // given
        Category category = new Category();
        ItemDto itemDto = new ItemDto(
                "AirForce",
                "AirForce",
                SaleStatus.ON_SALE,
                100000,
                LocalDateTime.now()
        );
        OptionDto optionDto = new OptionDto(
                "White240",
                5000
        );

        Item item = Item.fromDto(itemDto, category);
        Option option = Option.fromOption(optionDto);
        Product product = new Product(item, option);

        // when
        ProductDto dto = product.toDto();

        // then
        assertNull(dto.id(), "Not persisted, so id should be null.");
        assertEquals("AirForce", dto.itemName(), "Item name should match.");
        assertEquals("White240", dto.optionName(), "Option name should match.");
        assertEquals(105000.0, dto.finalPrice(), 0.001, "Final price should be 105000.0");
    }
}