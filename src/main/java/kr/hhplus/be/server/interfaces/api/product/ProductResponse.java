package kr.hhplus.be.server.interfaces.api.product;


import kr.hhplus.be.server.domain.common.Money;

public record ProductResponse(
        Long id,
        String itemName,
        String optionName,
        Money finalPrice
) {
}