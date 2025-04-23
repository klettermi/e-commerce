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
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testLookupProducts_withPaging() {
        // given
        // (테스트용 fixture 로 product1, product2 는 미리 @BeforeEach 등에서 생성해두었다고 가정)
        List<Product> products = List.of(product1, product2);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        PageImpl<Product> productPage = new PageImpl<>(products, pageable, products.size());

        // repository.findAll(Pageable) 모킹
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        // when
        Page<Product> page = productService.getProductList(pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(2);

        // 첫 번째 상품 검증
        Product p1 = page.getContent().get(0);
        assertThat(p1.getId()).isNull();  // 아직 영속화 전이므로 id 는 null
        assertThat(p1.getItem().getName()).isEqualTo("AirForce");
        assertThat(p1.getOption().getName()).isEqualTo("White240");
        // basePrice + additionalCost = 105000
        Money expectedPrice1 = new Money(BigDecimal.valueOf(105_000));
        assertThat(p1.getItem().getBasePrice().add(p1.getOption().getAdditionalCost()))
                .isEqualTo(expectedPrice1);

        // 두 번째 상품 검증
        Product p2 = page.getContent().get(1);
        assertThat(p2.getId()).isNull();
        assertThat(p2.getItem().getName()).isEqualTo("AirMax");
        assertThat(p2.getOption().getName()).isEqualTo("Black");
        Money expectedPrice2 = new Money(BigDecimal.valueOf(160_000));
        assertThat(p2.getItem().getBasePrice().add(p2.getOption().getAdditionalCost()))
                .isEqualTo(expectedPrice2);
    }
}