package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderRepo;
    private final OrderProductJpaRepository orderProductRepo;


}
