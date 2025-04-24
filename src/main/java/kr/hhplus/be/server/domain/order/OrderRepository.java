package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    void deleteAll();

    long count();

    List<Order> findAll();

    OrderProduct saveOrderProduct(OrderProduct orderProduct);

}
