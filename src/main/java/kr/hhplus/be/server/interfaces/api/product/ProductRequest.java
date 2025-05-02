package kr.hhplus.be.server.interfaces.api.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.product.ProductInput;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRequest {

    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class List {
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        private Integer page;

        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        private Integer size;

        public ProductInput.List toInput() {
            ProductInput.List in = new ProductInput.List();
            in.setPage(page);
            in.setSize(size);
            return in;
        }
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class TopSelling {
        @NotNull(message = "topN은 필수입니다.")
        @Min(value = 1, message = "topN은 1 이상이어야 합니다.")
        private Integer topN;

        public ProductInput.TopSelling toInput() {
            ProductInput.TopSelling in = new ProductInput.TopSelling();
            in.setTopN(topN);
            return in;
        }
    }
}
