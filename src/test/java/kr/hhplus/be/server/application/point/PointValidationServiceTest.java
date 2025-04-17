package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointValidationServiceTest {

    PointValidationService validator = new PointValidationService();

    @Test
    @DisplayName("충전 금액이 1000 이상이면 성공")
    void validateChargeAmount_Success() {
        assertThatCode(() -> validator.validate(new Money(BigDecimal.valueOf(1000)), TransactionType.CHARGE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("충전 금액이 1000 미만이면 IllegalArgumentException 발생")
    void validateChargeAmountTooLow() {
        assertThatThrownBy(() -> validator.validate(new Money(BigDecimal.valueOf(999)), TransactionType.CHARGE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000 포인트 이상");
    }

    @Test
    @DisplayName("충전 금액이 100000 초과면 예외 발생")
    void validateChargeAmountTooHigh() {
        assertThatThrownBy(() -> validator.validate(new Money(BigDecimal.valueOf(100001)), TransactionType.CHARGE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10만 포인트");
    }

    @Test
    @DisplayName("사용 금액이 100 이상이면 성공")
    void validateUseAmount_Success() {
        assertThatCode(() -> validator.validate(new Money(BigDecimal.valueOf(100)), TransactionType.USE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("사용 금액이 100 미만이면 IllegalArgumentException 발생")
    void validateUseAmountTooLow() {
        assertThatThrownBy(() -> validator.validate(new Money(BigDecimal.valueOf(99)), TransactionType.USE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100 포인트 이상");
    }

    @Test
    @DisplayName("유효하지 않은 타입이면 IllegalArgumentException 발생")
    void validateInvalidType() {
        TransactionType invalidType = null;
        assertThatThrownBy(() -> validator.validate(new Money(BigDecimal.valueOf(1000)), invalidType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 거래 타입");
    }
}
