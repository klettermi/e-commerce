package kr.hhplus.be.server.interfaces.api.order;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderProductRequest> orderProductRequests) {

        Order order = orderFacade.createOrder(userId, orderProductRequests);
        OrderResponse orderResponse = OrderResponse.from(order);

        return ApiResponse.success(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderFacade.getOrder(orderId);
        OrderResponse orderResponse = OrderResponse.from(order);

        return ApiResponse.success(orderResponse);
    }
}
