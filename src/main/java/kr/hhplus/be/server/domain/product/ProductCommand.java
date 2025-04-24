package kr.hhplus.be.server.domain.product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    @Getter
    public static class GetProductList {
        private final Pageable pageable;

        private GetProductList(Pageable pageable) {
            this.pageable = pageable;
        }

        public static GetProductList of(Pageable pageable) {
            return new GetProductList(pageable);
        }
    }

    @Getter
    public static class GetTopSelling {
        private final int topN;

        private GetTopSelling(int topN) {
            this.topN = topN;
        }

        public static GetTopSelling of(int topN) {
            return new GetTopSelling(topN);
        }
    }
}