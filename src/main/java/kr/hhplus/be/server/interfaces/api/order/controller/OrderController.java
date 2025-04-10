package kr.hhplus.be.server.interfaces.api.order.controller;


import kr.hhplus.be.server.application.order.OrderService;
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
    public ResponseEntity<?> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderProductRequest> orderProductRequests) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            String orderNumber = generateOrderNumber();
            Order order = orderService.placeOrder(user, orderNumber, orderProductRequests);

            return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(OrderResponse.from(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
