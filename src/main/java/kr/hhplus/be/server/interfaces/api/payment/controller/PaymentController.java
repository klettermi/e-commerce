package kr.hhplus.be.server.interfaces.api.payment.controller;


import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping("/{orderId}")
    public Map<String, Object> getPayment(@PathVariable Long orderId) {
        return Map.of("orderId", orderId, "status", "PAID", "paidAmount", 64000);
    }
}
