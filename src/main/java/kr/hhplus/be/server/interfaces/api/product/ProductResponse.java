package kr.hhplus.be.server.interfaces.api.product;


import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.product.Product;

public record ProductResponse(
        Long id,
        String itemName,
        String optionName,
        Money finalPrice
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.id,
                product.getItem().getName(),
                product.getOption().getName(),
                product.getItem().getBasePrice().add(product.getOption().getAdditionalCost())
        );
    }
}