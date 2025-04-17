package kr.hhplus.be.server.application.point;

import java.math.BigDecimal;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class PointValidationService {

    public void validate(Money amount, TransactionType type) {
        if (type == TransactionType.CHARGE) {
            // 충전은 최소 1000 포인트 이상이어야 함.
            if (amount.compareTo(new Money(new BigDecimal("1000"))) < 0) {
                throw new IllegalArgumentException("충전 금액은 1000 포인트 이상이어야 합니다.");
            }
            // 하루 최대 충전 금액은 10만 포인트
            if (amount.compareTo(new Money(new BigDecimal("100000"))) > 0) {
                throw new IllegalArgumentException("1일 충전 금액은 최대 10만 포인트입니다.");
            }
        } else if (type == TransactionType.USE) {
            // 사용은 최소 100 포인트 이상이어야 함.
            if (amount.compareTo(new Money(new BigDecimal("100"))) < 0) {
                throw new IllegalArgumentException("사용 금액은 100 포인트 이상이어야 합니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 거래 타입입니다.");
        }
    }
}
