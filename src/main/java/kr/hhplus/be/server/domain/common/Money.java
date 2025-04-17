package kr.hhplus.be.server.domain.common;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainExceptions.InvalidStateException("금액은 0 이상이어야 합니다.");
        }
    }

    public static Money of(int number) {
        return new Money(BigDecimal.valueOf(number));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }

    public int compareTo(Money zero) {
        return amount.compareTo(zero.amount);
    }
}
