package kr.hhplus.be.server.domain.cart;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.product.Product;
import lombok.*;

@Entity
@Table(name = "cart_item")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long productId;

    @Column
    private String productName;

    @Column
    @Setter
    private int quantity;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "price", nullable = false)
    )
    private Money price;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void assignCart(Cart cart) {
        this.cart = cart;
    }
}
