package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    void deleteAll();

    long count();

    List<Order> findAll();


}
