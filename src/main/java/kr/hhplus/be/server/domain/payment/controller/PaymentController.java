package kr.hhplus.be.server.domain.payment.controller;

import kr.hhplus.be.server.domain.payment.dto.PaymentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping("/{orderId}")
    public PaymentResponse getPayment(@PathVariable Long orderId) {
        return new PaymentResponse(orderId, "PAID", 64000);
    }
}
