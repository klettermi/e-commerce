package kr.hhplus.be.server.domain.cart;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.api.cart.dto.CartItemDto;
import lombok.*;

@Entity
@Table(name = "cart_items")
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

    @Column
    @Embedded
    private Money price;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 필요한 경우 Product에서 CartItem을 생성하는 생성자 구현 예시
    public CartItem(Product product, int quantity) {
        // product 객체가 null이 아님을 가정하고, 필요한 필드 값 할당
        this.productId = product.getId();
        this.quantity = quantity;
    }

    // DTO에서 엔티티로 변환하는 정적 메서드 (메서드 명 변경)
    public static CartItem fromDto(CartItemDto dto, Cart cart) {
        return CartItem.builder()
                .productId(dto.productId())
                .productName(dto.productName())
                .quantity(dto.quantity())
                .price(dto.price())
                .cart(cart)
                .build();
    }

    // Cart 할당 메서드 (필요한 경우)
    public void assignCart(Cart cart) {
        this.cart = cart;
    }
}
