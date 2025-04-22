package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    long countByUserId(Long userId);
}
