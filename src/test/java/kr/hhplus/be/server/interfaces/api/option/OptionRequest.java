package kr.hhplus.be.server.interfaces.api.option;

import kr.hhplus.be.server.domain.common.Money;

public record OptionRequest(
        String name,
        Money additionalCost
) { }