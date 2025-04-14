package kr.hhplus.be.server.interfaces.api.product.dto;


import kr.hhplus.be.server.domain.common.Money;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String itemName,
        String optionName,
        Money finalPrice
) {
}