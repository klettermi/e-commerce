package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import lombok.*;

@Entity
@Table(name = "order_products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "unit_point", nullable = false)
    )
    private Money unitPoint;
}
