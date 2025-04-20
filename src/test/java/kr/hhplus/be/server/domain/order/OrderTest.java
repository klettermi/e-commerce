package kr.hhplus.be.server.domain.order;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;
import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Order order;
    private OrderProduct orderProduct;

    private User dummyUser;

    @BeforeEach
    void setup() {
        dummyUser = new User();

        // 총 결제 금액을 2000으로 갖는 Money 객체 생성
        Money totalPoint = Money.of(2000);
        // 생성자에서 orderNumber, user, totalPoint, 초기 상태를 설정합니다.
        order = new Order(dummyUser, "ORD-TEST", totalPoint, OrderStatus.CREATED);

        // OrderProduct stub 생성: productId=1, quantity=2, unitPoint=500
        orderProduct = OrderProduct.builder()
                .productId(1L)
                .quantity(2)
                .unitPoint(Money.of(500))
                .build();
    }

    @Test
    void markAsPaid_success() {
        // 초기 상태는 CREATED여야 함
        assertEquals(OrderStatus.CREATED, order.getStatus(), "초기 상태는 CREATED여야 합니다.");

        // markAsPaid 호출
        order.markAsPaid();

        // 상태가 PAID로 변경되어야 함
        assertEquals(OrderStatus.PAID, order.getStatus(), "markAsPaid 호출 후 상태가 PAID여야 합니다.");
    }

    @Test
    void markAsPaid_invalidState_throwsException() {
        // 상태가 CREATED인 상태에서 한 번 결제 진행
        order.markAsPaid();
        Exception exception = assertThrows(InvalidStateException.class, () -> order.markAsPaid());
        assertEquals("결제 가능한 상태가 아닙니다.", exception.getMessage());
    }

    @Test
    void addOrderProduct_success() {
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
    void removeOrderProduct_success() {
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
