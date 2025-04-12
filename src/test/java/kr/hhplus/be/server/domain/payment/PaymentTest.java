package kr.hhplus.be.server.domain.payment;

import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.Test;

public class PaymentTest {

    @Test
    void payment_생성_및_필드_할당_테스트() {
        // Dummy Order 객체 생성 (실제 Order 내부 로직은 무시하고 필요 최소한의 객체로 사용)
        Order dummyOrder = new Order();
        // dummyOrder에 대해 필요한 값이 있다면, 별도의 setter 또는 reflection을 통해 설정할 수 있습니다.
        // 테스트에서는 단순히 객체 존재 여부를 확인합니다.

        // Payment 엔티티 인스턴스 생성 (기본 생성자 사용)
        Payment payment = new Payment();

        try {
            // "order" 필드에 dummyOrder 할당 (private 필드이므로 reflection 사용)
            java.lang.reflect.Field orderField = Payment.class.getDeclaredField("order");
            orderField.setAccessible(true);
            orderField.set(payment, dummyOrder);

            // "paymentAmount" 필드에 값 할당
            java.lang.reflect.Field amountField = Payment.class.getDeclaredField("paymentAmount");
            amountField.setAccessible(true);
            amountField.setInt(payment, 1500);

            // 검증: 할당된 값이 올바르게 설정되었는지 reflection으로 읽어 확인
            Order actualOrder = (Order) orderField.get(payment);
            int actualAmount = amountField.getInt(payment);

            assertNotNull(actualOrder, "Payment 객체의 order 필드는 null이 아니어야 합니다.");
            assertEquals(1500, actualAmount, "Payment 객체의 paymentAmount 필드 값이 1500이어야 합니다.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection을 통한 필드 접근에 실패했습니다: " + e.getMessage());
        }
    }
}
