package kr.hhplus.be.server.application.payment;

import jakarta.persistence.EntityExistsException;
import kr.hhplus.be.server.application.redis.RedissonLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final RedissonLockService lockService;
    private final PaymentTxnService paymentTxnService;
    private static final long DEFAULT_TTL_MS = 30_000;  // 30초

    public PaymentOutput.Result processPayment(PaymentInput.Process input) {
        String key = "payment:" + input.getOrderId();

        if (!lockService.tryLock(key, DEFAULT_TTL_MS)) {
            throw new EntityExistsException("이미 처리 중입니다.");
        }

        try {
            return paymentTxnService.processPayment(input);
        } finally {
            lockService.unlock(key);
        }
    }
}
