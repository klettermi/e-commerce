package kr.hhplus.be.server.interfaces.api.product.dto;


public record ProductDto(
        Long id,
        String itemName,
        String optionName,
        double finalPrice
) {
}