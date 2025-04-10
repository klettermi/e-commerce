package kr.hhplus.be.server.domain.order;

import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class OrderTest {

    private Order order;
    private OrderProduct orderProduct;

    // 단순 stub으로 사용할 User 객체
    private User dummyUser;

    @BeforeEach
    void setup() {
        // 간단한 stub User 객체 생성. (필요시 id 등을 설정)
        dummyUser = new User();
        // (테스트 목적에 따라, dummyUser에 대해 id 설정이 필요하면 Reflection 등을 사용할 수 있지만, 여기서는 비교하지 않습니다.)

        // 총 결제 금액을 2000으로 갖는 Money 객체 생성
        Money totalPoint = new Money(BigDecimal.valueOf(2000));
        // 생성자에서 orderNumber, user, totalPoint, 초기 상태를 설정합니다.
        order = new Order(dummyUser, "ORD-TEST", totalPoint, OrderStatus.CREATED);

        // OrderProduct stub 생성: productId=1, quantity=2, unitPoint=500
        orderProduct = OrderProduct.builder()
                .productId(1L)
                .quantity(2)
                .unitPoint(500)
                .build();
    }

    @Test
    void markAsPaid_정상처리() {
        // 초기 상태는 CREATED여야 함
        assertEquals(OrderStatus.CREATED, order.getStatus(), "초기 상태는 CREATED여야 합니다.");

        // markAsPaid 호출
        order.markAsPaid();

        // 상태가 PAID로 변경되어야 함
        assertEquals(OrderStatus.PAID, order.getStatus(), "markAsPaid 호출 후 상태가 PAID여야 합니다.");
    }

    @Test
    void markAsPaid_잘못된상태_예외발생() {
        // 상태가 CREATED인 상태에서 한 번 결제 진행
        order.markAsPaid();
        // 이후 다시 결제 호출 시 IllegalStateException이 발생해야 함
        Exception exception = assertThrows(IllegalStateException.class, () -> order.markAsPaid());
        assertEquals("결제 가능한 상태가 아닙니다.", exception.getMessage());
    }

    @Test
    void addOrderProduct_테스트() {
        // 초기에는 주문 항목 목록이 비어있어야 함
        assertTrue(order.getOrderProducts().isEmpty(), "초기 주문 항목 목록은 비어 있어야 합니다.");

        // 주문 항목 추가
        order.addOrderProduct(orderProduct);

        // 주문 항목 목록에 추가된 항목의 수 확인
        assertEquals(1, order.getOrderProducts().size(), "주문 항목 목록의 크기는 1이어야 합니다.");
        // 추가한 주문 항목의 order 필드가 현재 Order를 가리키는지 확인
        assertSame(order, orderProduct.getOrder(), "orderProduct의 order 필드가 현재 주문을 참조해야 합니다.");
    }

    @Test
    void removeOrderProduct_테스트() {
        // 먼저 주문 항목 추가
        order.addOrderProduct(orderProduct);
        assertEquals(1, order.getOrderProducts().size(), "주문 항목이 추가되어야 합니다.");

        // 주문 항목 삭제
        order.removeOrderProduct(orderProduct);

        // 주문 항목 목록이 비어있어야 하며, orderProduct의 order가 null이어야 합니다.
        assertTrue(order.getOrderProducts().isEmpty(), "주문 항목 목록은 삭제 후 비어 있어야 합니다.");
        assertNull(orderProduct.getOrder(), "삭제된 orderProduct의 order는 null이어야 합니다.");
    }
}
