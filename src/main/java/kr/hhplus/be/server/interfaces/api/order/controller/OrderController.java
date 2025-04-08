package kr.hhplus.be.server.interfaces.api.order.controller;


import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        return Map.of("orderId", 101, "totalAmount", 74000, "status", "PENDING");
    }

    @PostMapping("/{orderId}/pay")
    public Map<String, Object> pay(@PathVariable Long orderId, @RequestBody Map<String, Object> request) {
        return Map.of(
                "orderId", orderId,
                "status", "PAID",
                "paidAmount", 64000,
                "discount", 10000,
                "remainingBalance", 36000
        );
    }

    @GetMapping("/{orderId}")
    public Map<String, Object> getOrder(@PathVariable Long orderId) {
        return Map.of("orderId", orderId, "status", "PAID");
    }
}
