package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
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
    private Option<BaseEntity> option1;
    private Product product1;
    private Item item2;
    private Option<BaseEntity> option2;
    private Product product2;

    @BeforeEach
    void setUp() {
        category = new Category();

        // 첫 번째 상품: 할인 없음
         item1 = Item.builder()
                .name("AirForce")
                .description("AirForce Description")
                .category(category)
                .saleStartDate(LocalDateTime.now())
                .saleStatus(SaleStatus.ON_SALE)
                .basePrice(Money.of(100_000))
                .build();
        option1 = Option.builder()
                .name("White240")
                .additionalCost(Money.of(5_000))
                .build();
        product1 = new Product(item1, option1);  // 기본 최종 가격: 100000 + 5000 = 105000

        item2 = Item.builder()
                .name("AirMax")
                .description("AirMax Description")
                .category(category)
                .saleStartDate(LocalDateTime.now())
                .saleStatus(SaleStatus.ON_SALE)
                .basePrice(Money.of(100_000))
                .build();
        option2 = Option.builder()
                .name("Black")
                .additionalCost(Money.of(60_000))
                .build();

        product2 = new Product(item2, option2);
    }

    @Test
    void testLookupProducts() {
        // given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);


        // when
        List<Product> productList = productService.getProductList();


        // then
        assertEquals(2, productList.size(), "상품 목록의 크기는 2여야 합니다.");

        // 첫 번째 상품 검증
        Product responses1 = productList.get(0);
        // 아직 영속화되지 않았으므로 id는 null이어야 합니다.
        assertNull(responses1.getId(), "영속화되지 않은 상태면 첫 상품 id는 null이어야 합니다.");
        assertEquals("AirForce", responses1.getItem().getName(), "첫 상품의 itemName은 'AirForce'여야 합니다.");
        // 도메인에서 Option의 이름은 DTO 변환 시 optionName으로 노출됩니다.
        assertEquals("White240", responses1.getOption().getName(), "첫 상품의 optionName은 'White240'이어야 합니다.");
        assertEquals(new Money(BigDecimal.valueOf(105000)), responses1.getItem().getBasePrice().add(option1.getAdditionalCost()), "첫 상품의 최종 가격은 105000이어야 합니다.");
        // 두 번째 상품 검증
        Product responses2 = productList.get(1);
        assertNull(responses2.getId(), "영속화되지 않은 상태면 두 번째 상품 id는 null이어야 합니다.");
        assertEquals("AirMax", responses2.getItem().getName(), "두 번째 상품의 itemName은 'AirMax'여야 합니다.");
        assertEquals("Black", responses2.getOption().getName(), "두 번째 상품의 optionName은 'Black'이어야 합니다.");
        assertEquals(new Money(BigDecimal.valueOf(160000)), responses2.getItem().getBasePrice().add(responses2.getOption().getAdditionalCost()), "두 번째 상품의 최종 가격은 160000이어야 합니다.");
    }
}