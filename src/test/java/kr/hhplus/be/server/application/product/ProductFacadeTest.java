package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    @Test
    void getProductList_delegatesToService() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        Product p1 = Product.builder().id(1L).build();
        Product p2 = Product.builder().id(2L).build();
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);

        when(productService.getProductList(pageable)).thenReturn(page);

        Page<Product> result = productFacade.getProductList(pageable);

        assertThat(result).isSameAs(page);
        assertThat(result.getContent()).containsExactly(p1, p2);
        verify(productService).getProductList(pageable);
    }

    @Test
    void getTopSellingProductsLast3Days_delegatesToService() {
        List<Product> topList = List.of(
                Product.builder().id(10L).build(),
                Product.builder().id(20L).build()
        );

        when(productService.getTopSellingProductsLast3Days(2)).thenReturn(topList);

        List<Product> result = productFacade.getTopSellingProductsLast3Days(2);

        assertThat(result).isSameAs(topList);
        assertThat(result).hasSize(2).extracting(Product::getId)
                .containsExactlyInAnyOrder(10L, 20L);
        verify(productService).getTopSellingProductsLast3Days(2);
    }
}
