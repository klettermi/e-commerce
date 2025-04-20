package kr.hhplus.be.server.interfaces.api.payment;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> payOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long couponId) {
        Payment payment = paymentService.processPayment(orderId, userId, couponId);
        return ResponseEntity.ok(ApiResponse.success(PaymentResponse.from(payment)));
    }
}
