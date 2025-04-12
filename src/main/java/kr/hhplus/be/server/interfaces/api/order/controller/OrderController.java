package kr.hhplus.be.server.interfaces.api.order.controller;


import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
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

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderProductRequest> orderProductRequests) {

        OrderResponse orderResponse = orderFacade.createOrder(userId, orderProductRequests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderResponse));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderFacade.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }
}
