package kr.hhplus.be.server.interfaces.api.payment.controller;


import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.application.payment.PaymentService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<OrderResponse>> payOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long couponId) {
        Order order = paymentService.processPayment(orderId, userId, couponId);
        return ResponseEntity.ok(ApiResponse.success(OrderResponse.from(order)));
    }
}
