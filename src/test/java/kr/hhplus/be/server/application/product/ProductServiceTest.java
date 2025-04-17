package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.interfaces.api.item.ItemRequest;
import kr.hhplus.be.server.interfaces.api.option.OptionRequest;
import kr.hhplus.be.server.interfaces.api.product.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Item item1;
    private Option option1;
    private Product product1;
    private Item item2;
    private Option option2;
    private Product product2;

    @BeforeEach
    void setUp() {
        category = new Category();

        // 첫 번째 상품: 할인 없음
        ItemRequest itemRequest1 = new ItemRequest(
                "AirForce",            // item name
                "AirForce",            // item description
                SaleStatus.ON_SALE,
                new Money(BigDecimal.valueOf(100000)),                // basePrice
                LocalDateTime.now()
        );
        OptionRequest optionRequest1 = new OptionRequest(
                "White240",            // option name
                new Money(BigDecimal.valueOf(5000))                   // additionalCost
        );
        item1 = Item.fromDto(itemRequest1, category);
        option1 = Option.fromDto(optionRequest1);
        product1 = new Product(item1, option1);  // 기본 최종 가격: 100000 + 5000 = 105000

        ItemRequest itemRequest2 = new ItemRequest(
                "AirMax",
                "AirMax",
                SaleStatus.ON_SALE,
                new Money(BigDecimal.valueOf(100000)),
                LocalDateTime.now()
        );
        OptionRequest optionRequest2 = new OptionRequest(
                "Black",
                new Money(BigDecimal.valueOf(60000))
        );
        item2 = Item.fromDto(itemRequest2, category);
        option2 = Option.fromDto(optionRequest2);
        product2 = new Product(item2, option2);
    }

    @Test
    void testLookupProducts() {
        // given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);


        // when
        List<ProductResponse> dtos = productService.getProductList();

        // then
        assertEquals(2, dtos.size(), "상품 목록의 크기는 2여야 합니다.");

        // 첫 번째 상품 검증
        ProductResponse dto1 = dtos.get(0);
        // 아직 영속화되지 않았으므로 id는 null이어야 합니다.
        assertNull(dto1.id(), "영속화되지 않은 상태면 첫 상품 id는 null이어야 합니다.");
        assertEquals("AirForce", dto1.itemName(), "첫 상품의 itemName은 'AirForce'여야 합니다.");
        // 도메인에서 Option의 이름은 DTO 변환 시 optionName으로 노출됩니다.
        assertEquals("White240", dto1.optionName(), "첫 상품의 optionName은 'White240'이어야 합니다.");
        assertEquals(0, new Money(BigDecimal.valueOf(105000)).compareTo(dto1.finalPrice()), "첫 상품의 최종 가격은 105000이어야 합니다.");
        // 두 번째 상품 검증
        ProductResponse dto2 = dtos.get(1);
        assertNull(dto2.id(), "영속화되지 않은 상태면 두 번째 상품 id는 null이어야 합니다.");
        assertEquals("AirMax", dto2.itemName(), "두 번째 상품의 itemName은 'AirMax'여야 합니다.");
        assertEquals("Black", dto2.optionName(), "두 번째 상품의 optionName은 'Black'이어야 합니다.");
        assertEquals(0, new Money(BigDecimal.valueOf(160000)).compareTo(dto2.finalPrice()), "두 번째 상품의 최종 가격은 160000이어야 합니다.");
    }
}