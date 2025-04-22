package kr.hhplus.be.server.application.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.point.UserPointRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PointServiceTest {

    @Mock PointRepository pointRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    PointService pointService;

    private final long USER_ID = 42L;

    private User DUMMY_USER = User.builder()
            .id(USER_ID)
            .username("username")
            .build();
    private UserPoint DUMMY_USER_POINT = UserPoint.builder()
            .user(DUMMY_USER)
            .pointBalance(Money.of(100))
            .build();


    @AfterEach
    void clean() {
        pointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getPoint_whenNotExists_throwsNotFound() {
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pointService.getPoint(USER_ID));
    }

    @Test
    void getPointHistory_returnsAll() {
        PointHistory h1 = PointHistory.createChargeHistory(DUMMY_USER, Money.of(500));
        PointHistory h2 = PointHistory.createUseHistory(DUMMY_USER, Money.of(200));
        when(pointRepository.findByUserId(USER_ID)).thenReturn(List.of(h1, h2));

        List<PointHistory> pointHistoryList = pointService.getPointHistory(USER_ID);

        assertEquals(2, pointHistoryList.size());
        assertTrue(pointHistoryList.stream().anyMatch(r -> r.getType().name().equals(h1.getType().name())&& r.getAmount().equals(h1.getAmount())));
        assertTrue(pointHistoryList.stream().anyMatch(r -> r.getType().name().equals(h2.getType().name()) && r.getAmount().equals(h2.getAmount())));
        verify(pointRepository).findByUserId(USER_ID);
    }

    @Test
    void chargePoint_valid_savesAndReturns() {
        Money amount = Money.of(1300);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        Money originBalance = DUMMY_USER_POINT.getPointBalance();

        UserPoint userPoint = pointService.chargePoint(USER_ID, amount);

        assertEquals(USER_ID, userPoint.getId());
        assertEquals(amount.add(originBalance), userPoint.getPointBalance());
        InOrder ord = inOrder(userRepository, pointRepository);
        ord.verify(userRepository).findById(USER_ID);
        ord.verify(pointRepository, times(1)).save((UserPoint) any());
        ord.verify(pointRepository, times(1)).save((PointHistory) any());
    }

    @Test
    void chargePoint_whenUserNotFound_throwsNotFound() {
        Money amount = Money.of(100);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pointService.chargePoint(USER_ID, amount));
    }

    @Test
    void usePoint_valid_savesAndReturns() {
        Money amount = Money.of(200);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        UserPoint userPoint = pointService.usePoint(USER_ID, amount);

        assertEquals(USER_ID, userPoint.getUser().getId());
        assertEquals(Money.of(800), userPoint.getUser().getUserPoint());
        InOrder ord = inOrder( userRepository, pointRepository);
        ord.verify(userRepository).findById(USER_ID);
        ord.verify(pointRepository, times(1)).save((UserPoint) any());
        ord.verify(pointRepository, times(1)).save((PointHistory) any());
    }

    @Test
    void usePoint_insufficient_throwsIllegalArgument() {
        Money amount = Money.of(1200);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        assertThrows(DomainException.InvalidStateException.class,
                () -> pointService.usePoint(USER_ID, amount));
    }
}
