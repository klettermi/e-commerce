package kr.hhplus.be.server.api.item.dto;

import kr.hhplus.be.server.domain.item.SaleStatus;

import java.time.LocalDateTime;

public record ItemDto (
        String name,
        String description,
        SaleStatus saleStatus,
        Integer basePrice,
        LocalDateTime saleStartDate
){
}
