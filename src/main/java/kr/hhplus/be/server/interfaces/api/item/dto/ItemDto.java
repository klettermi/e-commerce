package kr.hhplus.be.server.interfaces.api.item.dto;

import kr.hhplus.be.server.domain.item.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemDto (
        String name,
        String description,
        SaleStatus saleStatus,
        BigDecimal basePrice,
        LocalDateTime saleStartDate
){
}
