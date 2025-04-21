package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderRepo;
    private final OrderProductJpaRepository orderProductRepo;

    @Override
    public Order save(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderRepo.findById(orderId);
    }

    @Override
    public long countByUserId(User user) {
        return orderRepo.countByUser(user);
    }

    @Override
    public void deleteAll() {
        orderRepo.deleteAll();
    }

    @Override
    public long count() {
        return orderRepo.count();
    }
}
