package kr.hhplus.be.server.infrastructure.cart;

import kr.hhplus.be.server.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemJpaRepository extends JpaRepository<CartItem, Long> {
}
