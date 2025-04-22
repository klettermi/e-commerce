package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "`order`")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column(name = "order_number", length = 50, nullable = false, unique = true)
    private String orderNumber;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "total_point", nullable = false)
    )
    private Money totalPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public Order(Long userId, String orderNumber, Money totalPoint, OrderStatus status) {
        super();
        this.userId = userId;
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

    public void markAsPaid() {
        if (!OrderStatus.CREATED.equals(this.status)) {
            throw new DomainException.InvalidStateException("결제 가능한 상태가 아닙니다.");
        }
        this.status = OrderStatus.PAID;
    }
}
