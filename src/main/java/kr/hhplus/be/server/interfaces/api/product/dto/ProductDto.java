package kr.hhplus.be.server.interfaces.api.product.dto;


import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String itemName,
        String optionName,
        BigDecimal finalPrice
) {
}