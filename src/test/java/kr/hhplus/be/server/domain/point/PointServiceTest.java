package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PointService pointService;

    private final Long USER_ID = 11L;
    private User user;
    private UserPoint userPoint;
    private Money amount;
    private Money chargeAmount;

    @BeforeEach
    void setUp() {
        // 공통 사용자/포인트 준비
        user = User.builder().id(USER_ID).build();

        userPoint = new UserPoint();
        userPoint.setUser(user);
        userPoint.setPointBalance(Money.of(1000));  // 초기 1000점

        amount = Money.of(500);
        chargeAmount = Money.of(1000);
    }

    @Test
    void getPoint_whenExists_returnsUserPoint() {
        when(pointRepository.findById(USER_ID))
                .thenReturn(Optional.of(userPoint));

        UserPoint result = pointService.getPoint(USER_ID);

        assertSame(userPoint, result);
        verify(pointRepository).findById(USER_ID);
    }

    @Test
    void getPoint_whenNotExists_throwsEntityNotFoundException() {
        when(pointRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> pointService.getPoint(USER_ID)
        );
        assertTrue(ex.getMessage().contains("UserPoint not found for id"));
        verify(pointRepository).findById(USER_ID);
    }

    @Test
    void getPointHistory_returnsList() {
        PointHistory h1 = PointHistory.createChargeHistory(user.getId(), amount);
        PointHistory h2 = PointHistory.createUseHistory(user.getId(), amount);
        when(pointRepository.findByUserIdHistory(USER_ID))
                .thenReturn(List.of(h1, h2));

        List<PointHistory> history = pointService.getPointHistory(USER_ID);

        assertEquals(2, history.size());
        assertTrue(history.contains(h1) && history.contains(h2));
    }

    @Test
    void chargePoint_success() {
        UserPoint spyPoint = spy(userPoint);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(spyPoint));
        when(pointRepository.save(any(UserPoint.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(pointRepository.save(any(PointHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserPoint result = pointService.chargePoint(USER_ID, chargeAmount);

        // 도메인 메서드들이 호출됐는지
        verify(spyPoint).validate(chargeAmount, TransactionType.CHARGE);
        verify(spyPoint).chargePoints(chargeAmount);

        // 저장 호출 검증 (UserPoint, PointHistory 각각 한 번씩)
        verify(pointRepository).save(spyPoint);
        verify(pointRepository).save(isA(PointHistory.class));

        // 반환된 포인트는 기존 1000 + 500 = 1500
        assertEquals(Money.of(2000), result.getPointBalance());
    }

    @Test
    void chargePoint_userNotFound_throwsEntityNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> pointService.chargePoint(USER_ID, amount)
        );
        assertTrue(ex.getMessage().contains("User not found for id"));
        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(pointRepository);
    }

    @Test
    void chargePoint_userPointNotFound_throwsInvalidState() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.empty());

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> pointService.chargePoint(USER_ID, amount)
        );
        assertTrue(ex.getMessage().contains("UserPoint not found for id"));
        verify(pointRepository).findById(USER_ID);
    }

    @Test
    void usePoint_success() {
        UserPoint spyPoint = spy(userPoint);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(USER_ID)).thenReturn(Optional.of(spyPoint));
        when(pointRepository.save(any(UserPoint.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(pointRepository.save(any(PointHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserPoint result = pointService.usePoint(USER_ID, amount);

        verify(spyPoint).validate(amount, TransactionType.USE);
        verify(spyPoint).usePoints(amount);
        verify(pointRepository).save(spyPoint);
        verify(pointRepository).save(isA(PointHistory.class));

        // 반환된 포인트는 기존 1000 - 500 = 500
        assertEquals(Money.of(500), result.getPointBalance());
    }

    @Test
    void usePoint_userNotFound_throwsEntityNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> pointService.usePoint(USER_ID, amount)
        );
        assertTrue(ex.getMessage().contains("User not found for id"));
    }

    @Test
    void usePoint_userPointNotFound_throwsEntityNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> pointService.usePoint(USER_ID, amount)
        );
        assertTrue(ex.getMessage().contains("UserPoint not found for id"));
    }

    @Test
    void findByUserId_success() {
        when(pointRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(userPoint));

        UserPoint found = pointService.findByUserId(USER_ID);

        assertSame(userPoint, found);
        verify(pointRepository).findByUserId(USER_ID);
    }

    @Test
    void findByUserId_notFound_throwsEntityNotFound() {
        when(pointRepository.findByUserId(USER_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> pointService.findByUserId(USER_ID)
        );
        assertTrue(ex.getMessage().contains("UserPoint not found for user id"));
    }

    @Test
    void save_delegatesToRepository() {
        when(pointRepository.save(userPoint)).thenReturn(userPoint);

        UserPoint saved = pointService.save(userPoint);

        assertSame(userPoint, saved);
        verify(pointRepository).save(userPoint);
    }
}
