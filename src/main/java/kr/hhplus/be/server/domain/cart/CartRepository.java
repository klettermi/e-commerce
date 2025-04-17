package kr.hhplus.be.server.domain.cart;

import java.util.Optional;

public interface CartRepository{
    Optional<Cart> findByUserId(Long userId);

    Cart save(Cart cart);
}
