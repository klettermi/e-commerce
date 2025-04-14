package kr.hhplus.be.server.interfaces.api.option.dto;

import kr.hhplus.be.server.domain.common.Money;

import java.math.BigDecimal;

public record OptionDto(
        String name,
        Money additionalCost
) { }