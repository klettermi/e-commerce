package kr.hhplus.be.server.infrastructure.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
