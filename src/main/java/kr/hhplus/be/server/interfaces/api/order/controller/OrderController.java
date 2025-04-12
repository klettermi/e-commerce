package kr.hhplus.be.server.interfaces.api.order.controller;


import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderProductRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderProductRequest> orderProductRequests) {

        // 사용자 조회: 존재하지 않으면 도메인 예외(EntityNotFoundException) 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainExceptions.EntityNotFoundException("User not found with id: " + userId));

        String orderNumber = generateOrderNumber();
        Order order = orderService.placeOrder(user, orderNumber, orderProductRequests);

        OrderResponse orderResponse = OrderResponse.from(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderResponse));
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(OrderResponse.from(order)));
    }
}
