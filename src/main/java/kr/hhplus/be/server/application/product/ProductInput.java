package kr.hhplus.be.server.application.product;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProductInput {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        @Min(0) private int page;
        @Min(1) private int size;
    }

    @Getter
    @NoArgsConstructor
    @Setter
    public static class TopSelling {
        @Min(1) private int topN;
    }
}
