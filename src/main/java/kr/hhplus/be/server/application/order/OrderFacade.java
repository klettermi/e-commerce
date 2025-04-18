package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderProductRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderResponse createOrder(Long userId, List<OrderProductRequest> orderProductRequests) {
        // 사용자 조회: 존재하지 않으면 도메인 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainExceptions.EntityNotFoundException("User not found with id: " + userId));

        String orderNumber = generateOrderNumber();
        Order order = orderService.placeOrder(user, orderNumber, orderProductRequests);
        return OrderResponse.from(order);
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return OrderResponse.from(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}