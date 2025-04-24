package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void savePayment_delegatesToRepository_andReturnsSaved() {
        // given
        Payment payment = new Payment();  // JPA 엔티티의 기본 생성자 사용
        when(paymentRepository.save(payment)).thenReturn(payment);

        // when
        Payment result = paymentService.savePayment(payment);

        // then
        assertSame(payment, result, "저장된 Payment 객체를 그대로 반환해야 합니다.");
        verify(paymentRepository).save(payment);
    }
}
