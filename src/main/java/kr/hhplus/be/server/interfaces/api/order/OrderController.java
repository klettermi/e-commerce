package kr.hhplus.be.server.interfaces.api.order;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderProductRequest> orderProductRequests) {
        List<OrderProduct> orderProducts = orderProductRequests.stream().map(OrderProductRequest::toOrderProduct).collect(Collectors.toList());
        Order order = orderFacade.createOrder(userId, orderProducts);
        OrderResponse orderResponse = OrderResponse.fromEntity(order);
        return ApiResponse.success(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderFacade.getOrder(orderId);
        OrderResponse orderResponse = OrderResponse.fromEntity(order);
        return ApiResponse.success(orderResponse);
    }
}
