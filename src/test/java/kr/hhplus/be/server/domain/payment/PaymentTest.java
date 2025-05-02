package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void payment_create_test() {
        Order dummyOrder = new Order();

        Payment payment = new Payment();

        try {
            Field orderField = Payment.class.getDeclaredField("order");
            orderField.setAccessible(true);
            orderField.set(payment, dummyOrder);

            Field amountField = Payment.class.getDeclaredField("paymentAmount");
            amountField.setAccessible(true);
            Money moneyValue = Money.of(1500);
            amountField.set(payment, moneyValue);

            // 할당된 값 검증
            Order actualOrder = (Order) orderField.get(payment);
            Money actualMoney = (Money) amountField.get(payment);

            assertNotNull(actualOrder, "Payment 객체의 order 필드는 null이 아니어야 합니다.");
            assertEquals(BigDecimal.valueOf(1500), actualMoney.amount(), "Payment 객체의 paymentAmount 필드 값이 1500이어야 합니다.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection을 통한 필드 접근에 실패했습니다: " + e.getMessage());
        }
    }
}
