package kr.hhplus.be.server.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_number", length = 50, nullable = false, unique = true)
    private String orderNumber;

    @Embedded
    private Money totalPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    // 수정된 생성자: 인스턴스 필드를 초기화하도록 함.
    public Order(User user, String orderNumber, Money totalPoint, OrderStatus status) {
        super();
        this.user = user;
        this.orderNumber = orderNumber;
        this.totalPoint = totalPoint;
        this.status = status;
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void removeOrderProduct(OrderProduct orderProduct) {
        orderProducts.remove(orderProduct);
        orderProduct.setOrder(null);
    }

    // 주문의 상태를 변경하는 비즈니스 메서드 예시
    public void markAsPaid() {
        if (!OrderStatus.CREATED.equals(this.status)) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }
        this.status = OrderStatus.PAID;
    }
}
