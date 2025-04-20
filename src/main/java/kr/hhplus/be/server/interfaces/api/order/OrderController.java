package kr.hhplus.be.server.interfaces.api.order;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
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

        OrderResponse orderResponse = orderFacade.createOrder(userId, orderProductRequests);
        return ApiResponse.success(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderFacade.getOrder(orderId);
        return ApiResponse.success(orderResponse);
    }
}
