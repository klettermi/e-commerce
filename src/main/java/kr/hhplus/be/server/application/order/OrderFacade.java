package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserRepository userJpaRepository;

    public OrderResponse createOrder(Long userId, List<OrderProductRequest> orderProductRequests) {
        // 사용자 조회: 존재하지 않으면 도메인 예외 발생
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        String orderNumber = generateOrderNumber();
        Order order = orderService.placeOrder(user, orderNumber, orderProductRequests);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse getOrder(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return OrderResponse.from(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}