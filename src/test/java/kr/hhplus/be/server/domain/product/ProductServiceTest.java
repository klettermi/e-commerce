package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductList_returnsPageFromRepository() {
        // given
        Product p1 = Product.builder().id(1L).build();
        Product p2 =  Product.builder().id(2L).build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<Product> result = productService.getProductList(pageable);

        // then
        assertSame(page, result);
        assertEquals(2, result.getTotalElements());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getTopSellingProductsLast3Days_emptyRows_returnsEmptyList() {
        // given
        when(productRepository.findTopProductSince(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        // when
        List<Product> result = productService.getTopSellingProductsLast3Days(5);

        // then
        assertTrue(result.isEmpty());
        verify(productRepository).findTopProductSince(any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getTopSellingProductsLast3Days_success_returnsProducts() {
        // given
        Object[] row1 = new Object[]{1L};
        Object[] row2 = new Object[]{2L};
        List<Object[]> rows = List.of(row1, row2);
        when(productRepository.findTopProductSince(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(rows);

        Product prod1 = Product.builder().id(1L).build();
        Product prod2 = Product.builder().id(2L).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(prod1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(prod2));

        // when
        List<Product> result = productService.getTopSellingProductsLast3Days(2);

        // then
        assertEquals(2, result.size());
        assertEquals(prod1, result.get(0));
        assertEquals(prod2, result.get(1));
        verify(productRepository).findTopProductSince(any(LocalDateTime.class), any(PageRequest.class));
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
    }

    @Test
    void getTopSellingProductsLast3Days_idNotFound_throwsException() {
        // given
        Object[] row = new Object[]{99L};
        when(productRepository.findTopProductSince(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.<Object[]>of(row));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> productService.getTopSellingProductsLast3Days(1)
        );
        assertTrue(ex.getMessage().contains("상품을 찾을 수 없습니다. id=99"));
        verify(productRepository).findById(99L);
    }
}
