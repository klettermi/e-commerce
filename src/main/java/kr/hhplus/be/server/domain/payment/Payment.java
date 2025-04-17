package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.api.payment.PaymentResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@Getter
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "payment_amount", nullable = false)
    )
    private Money paymentAmount;

    public Payment(Order order, Money paymentAmount) {
        this.order = order;
        this.paymentAmount = paymentAmount;
    }

    public static Payment toEntity(PaymentResponse paymentResponse, Order order) {
        return new Payment(order, paymentResponse.paidAmount());
    }
}
