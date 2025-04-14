package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @Column(name = "payment_amount", nullable = false)
    @Embedded
    private Money paymentAmount;

    public Payment(Order order, Money paymentAmount) {
        this.order = order;
        this.paymentAmount = paymentAmount;
    }

    public static Payment toEntity(PaymentDto paymentDto, Order order) {
        return new Payment(order, paymentDto.paidAmount());
    }
}
