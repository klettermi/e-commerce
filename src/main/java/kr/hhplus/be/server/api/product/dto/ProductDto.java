package kr.hhplus.be.server.api.product.dto;


public record ProductDto(
        Long id,
        String itemName,
        String optionName,
        double finalPrice
) {
}