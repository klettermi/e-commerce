package kr.hhplus.be.server.interfaces.api.order;


import jakarta.validation.Valid;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderInput;
import kr.hhplus.be.server.application.order.OrderOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderInput.Get input = new OrderInput.Get(orderId);
        OrderOutput output = orderFacade.getOrder(input);
        return ApiResponse.success(OrderResponse.fromOutput(output));
    }

    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest.Place request
    ) {
        OrderOutput output = orderFacade.placeOrder(request.toInput());
        return ApiResponse.success(OrderResponse.fromOutput(output));
    }
}
