package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("""
    SELECT op.productId AS productId,
           SUM(op.quantity)   AS totalQty
    FROM OrderProduct op
    WHERE op.createdAt >= :startDate
    GROUP BY op.productId
    ORDER BY totalQty DESC
    """)
    List<Object[]> findTopProductSince(LocalDateTime startDate, Pageable pageable);
}
