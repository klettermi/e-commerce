package kr.hhplus.be.server.interfaces.api.payment;


import jakarta.validation.Valid;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.payment.PaymentTxnService;
import kr.hhplus.be.server.application.payment.PaymentInput;
import kr.hhplus.be.server.application.payment.PaymentOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Validated
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentTxnService paymentFacade;

    @PostMapping
    public ApiResponse<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest.Process request
    ) {
        PaymentInput.Process input = request.toInput();
        PaymentOutput.Result output = paymentFacade.processPayment(input);
        return ApiResponse.success(PaymentResponse.fromOutput(output));
    }
}