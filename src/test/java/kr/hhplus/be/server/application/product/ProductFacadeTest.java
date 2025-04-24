package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    @BeforeEach
    void setUp() {
        // nothing to init
    }

    @Test
    void getProductList_mapsCorrectly() {
        // given
        int page = 1, size = 3;
        // DTO 입력 생성
        ProductInput.List input = new ProductInput.List();
        ReflectionTestUtils.setField(input, "page", page);
        ReflectionTestUtils.setField(input, "size", size);

        Pageable expectedPageable = PageRequest.of(page, size);

        // 도메인 응답 stub 준비
        ProductInfo.ProductPage info = mock(ProductInfo.ProductPage.class);
        when(info.getPage()).thenReturn(page);
        when(info.getSize()).thenReturn(size);
        when(info.getTotalElements()).thenReturn(42L);

        // 두 개의 상품 정보 stub
        ProductInfo.ProductDetail p1 = mock(ProductInfo.ProductDetail.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getName()).thenReturn("Widget");
        when(p1.getBasePrice()).thenReturn(new BigDecimal("99.99"));

        ProductInfo.ProductDetail p2 = mock(ProductInfo.ProductDetail.class);
        when(p2.getId()).thenReturn(20L);
        when(p2.getName()).thenReturn("Gadget");
        when(p2.getBasePrice()).thenReturn(new BigDecimal("149.50"));

        when(info.getProducts()).thenReturn(List.of(p1, p2));

        // 서비스 호출 stub
        when(productService.getProductList(argThat(cmd ->
                cmd != null
                        && ((ProductCommand.GetProductList)cmd).getPageable().equals(expectedPageable)
        ))).thenReturn(info);

        // when
        ProductOutput.Page output = productFacade.getProductList(input);

        // then: 페이징 정보
        assertThat(output.getPage()).isEqualTo(page);
        assertThat(output.getSize()).isEqualTo(size);
        assertThat(output.getTotalElements()).isEqualTo(42L);

        // then: 아이템 매핑
        assertThat(output.getProducts()).hasSize(2);
        var out1 = output.getProducts().get(0);
        assertThat(out1.getId()).isEqualTo(10L);
        assertThat(out1.getName()).isEqualTo("Widget");
        assertThat(out1.getBasePrice()).isEqualByComparingTo("99.99");

        var out2 = output.getProducts().get(1);
        assertThat(out2.getId()).isEqualTo(20L);
        assertThat(out2.getName()).isEqualTo("Gadget");
        assertThat(out2.getBasePrice()).isEqualByComparingTo("149.50");

        verify(productService).getProductList(any(ProductCommand.GetProductList.class));
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getTopSellingProductsLast3Days_mapsCorrectly() {
        // given
        int topN = 5;
        ProductInput.TopSelling input = new ProductInput.TopSelling();
        ReflectionTestUtils.setField(input, "topN", topN);

        // 도메인 응답 stub
        ProductInfo.TopSellingList info = mock(ProductInfo.TopSellingList.class);

        // 상품 stub
        ProductInfo.ProductDetail tp1 = mock(ProductInfo.ProductDetail.class);
        when(tp1.getId()).thenReturn(100L);
        when(tp1.getName()).thenReturn("HotItem");
        when(tp1.getBasePrice()).thenReturn(new BigDecimal("200.00"));

        when(info.getProducts()).thenReturn(List.of(tp1));

        // 서비스 호출 stub
        when(productService.getTopSellingProductsLast3Days(argThat(cmd ->
                cmd != null
                        && ((ProductCommand.GetTopSelling)cmd).getTopN() == topN
        ))).thenReturn(info);

        // when
        ProductOutput.TopSellingList output = productFacade.getTopSellingProductsLast3Days(input);

        // then
        assertThat(output.getProducts()).hasSize(1);
        var o = output.getProducts().get(0);
        assertThat(o.getId()).isEqualTo(100L);
        assertThat(o.getName()).isEqualTo("HotItem");
        assertThat(o.getBasePrice()).isEqualByComparingTo("200.00");

        verify(productService).getTopSellingProductsLast3Days(any(ProductCommand.GetTopSelling.class));
        verifyNoMoreInteractions(productService);
    }
}
