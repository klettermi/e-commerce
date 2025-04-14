package kr.hhplus.be.server.domain.payment;

import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class PaymentTest {

    @Test
    void payment_생성_및_필드_할당_테스트() {
        Order dummyOrder = new Order();

        Payment payment = new Payment();

        try {
            // order 필드에 dummyOrder 할당
            Field orderField = Payment.class.getDeclaredField("order");
            orderField.setAccessible(true);
            orderField.set(payment, dummyOrder);

            // paymentAmount 필드에 Money 타입의 값을 할당해야 함
            Field amountField = Payment.class.getDeclaredField("paymentAmount");
            amountField.setAccessible(true);
            // BigDecimal 대신 Money 객체 생성 후 할당
            Money moneyValue = new Money(BigDecimal.valueOf(1500));
            amountField.set(payment, moneyValue);

            // 할당된 값 검증
            Order actualOrder = (Order) orderField.get(payment);
            Money actualMoney = (Money) amountField.get(payment);

            assertNotNull(actualOrder, "Payment 객체의 order 필드는 null이 아니어야 합니다.");
            // Money 클래스에서 equals() 메서드를 적절히 오버라이드했다면 이렇게 비교할 수도 있습니다.
            // assertEquals(moneyValue, actualMoney, "Payment 객체의 paymentAmount 필드 값이 1500이어야 합니다.");
            // equals가 제대로 작동하지 않는 경우, 내부 BigDecimal 값을 비교합니다.
            assertEquals(BigDecimal.valueOf(1500), actualMoney.amount(), "Payment 객체의 paymentAmount 필드 값이 1500이어야 합니다.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection을 통한 필드 접근에 실패했습니다: " + e.getMessage());
        }
    }
}
