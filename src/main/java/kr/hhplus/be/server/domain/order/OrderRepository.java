package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.user.User;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    long countByUserId(User userId);

    void deleteAll();
}
