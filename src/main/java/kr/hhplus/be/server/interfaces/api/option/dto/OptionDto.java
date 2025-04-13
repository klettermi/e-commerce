package kr.hhplus.be.server.interfaces.api.option.dto;

import java.math.BigDecimal;

public record OptionDto(
        String name,
        BigDecimal additionalCost
) { }