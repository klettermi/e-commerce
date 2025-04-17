package kr.hhplus.be.server.infrastructure.cart;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaRepository cartRepo;
    private final CartItemJpaRepository cartItemRepo;

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    @Override
    public Cart save(Cart cart) {
        return cartRepo.save(cart);
    }
}
