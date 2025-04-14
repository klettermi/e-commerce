package kr.hhplus.be.server.domain.cart;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(Long userId) {
        this.userId = userId;
    }

    public void addItemInCart(CartItem item) {
        item.assignCart(this);  // 패키지-프라이빗 메서드를 통해 cart 설정
        cartItems.add(item);
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
        item.assignCart(null);
    }
}
