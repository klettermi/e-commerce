package kr.hhplus.be.server.interfaces.api.item;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.item.SaleStatus;

import java.time.LocalDateTime;

public record ItemRequest(
        String name,
        String description,
        SaleStatus saleStatus,
        Money basePrice,
        LocalDateTime saleStartDate
){
}
