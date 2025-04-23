package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testCalculateFinalPriceWithoutDiscount() {
        // given
        Category category = new Category();
        Item item = Item.builder()
                .name("AirMax")
                .description("AirMax Description")
                .category(category)
                .saleStartDate(LocalDateTime.now())
                .saleStatus(SaleStatus.ON_SALE)
                .basePrice(Money.of(100_000))
                .build();
        Option option = Option.builder()
                .name("Black")
                .additionalCost(Money.of(60_000))
                .build();
        Product product = new Product(item, option);

        // when
        Money finalPrice = product.calculateFinalPrice();

        // then
        assertEquals(Money.of(10500), finalPrice, "상품의 가격은 10500 이어야 합니다.");
    }

}