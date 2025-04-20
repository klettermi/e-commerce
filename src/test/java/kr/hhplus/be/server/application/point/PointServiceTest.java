package kr.hhplus.be.server.application.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.point.UserPointRequest;
import kr.hhplus.be.server.infrastructure.user.UserRequest;
import kr.hhplus.be.server.interfaces.api.point.PointHistoryResponse;
import kr.hhplus.be.server.interfaces.api.point.PointResponse;
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

    @Mock
    PointValidationService validationService;
    @InjectMocks
    PointService pointService;

    private final long USER_ID = 42L;

    // User DTO and UserPoint DTO
    private final UserRequest DUMMY_USER_DTO = new UserRequest(USER_ID, "username", "password");
    private final UserPointRequest DUMMY_POINT_DTO = new UserPointRequest(USER_ID, Money.of(1000));

    private User      DUMMY_USER;
    private UserPoint DUMMY_USER_POINT;

    @BeforeEach
    void setup() {
        // DTO → 도메인 엔티티 변환
        DUMMY_USER       = User.fromDto(DUMMY_USER_DTO);
        DUMMY_USER_POINT = UserPoint.fromDto(DUMMY_POINT_DTO, DUMMY_USER);
    }

    @Test
    void getPoint_whenExists_returnsDto() {
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        PointResponse resp = pointService.getPoint(USER_ID);

        assertEquals(USER_ID, resp.userId());
        assertEquals(DUMMY_USER_POINT.getPointBalance(), resp.point());
        verify(pointRepository).findById(USER_ID);
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

        List<PointHistoryResponse> list = pointService.getPointHistory(USER_ID);

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> r.transactionType().equals(h1.getType().name())&& r.changeAmount().equals(h1.getAmount())));
        assertTrue(list.stream().anyMatch(r -> r.transactionType().equals(h2.getType().name()) && r.changeAmount().equals(h2.getAmount())));
        verify(pointRepository).findByUserId(USER_ID);
    }

    @Test
    void chargePoint_valid_savesAndReturns() {
        Money amount = Money.of(300);

        when(validationService.validate(amount, TransactionType.CHARGE)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        PointResponse resp = pointService.chargePoint(USER_ID, amount);

        assertEquals(USER_ID, resp.userId());
        assertEquals(Money.of(1300), resp.point());
        InOrder ord = inOrder(validationService, userRepository, pointRepository);
        ord.verify(validationService).validate(amount, TransactionType.CHARGE);
        ord.verify(userRepository).findById(USER_ID);
        ord.verify(pointRepository, times(1)).save((UserPoint) any());
        ord.verify(pointRepository, times(1)).save((PointHistory) any());
    }

    @Test
    void chargePoint_whenUserNotFound_throwsNotFound() {
        Money amount = Money.of(100);
        when(validationService.validate(amount, TransactionType.CHARGE)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pointService.chargePoint(USER_ID, amount));
    }

    @Test
    void usePoint_valid_savesAndReturns() {
        Money amount = Money.of(200);

        when(validationService.validate(amount, TransactionType.USE)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        PointResponse resp = pointService.usePoint(USER_ID, amount);

        assertEquals(USER_ID, resp.userId());
        assertEquals(Money.of(800), resp.point());
        InOrder ord = inOrder(validationService, userRepository, pointRepository);
        ord.verify(validationService).validate(amount, TransactionType.USE);
        ord.verify(userRepository).findById(USER_ID);
        ord.verify(pointRepository, times(1)).save((UserPoint) any());
        ord.verify(pointRepository, times(1)).save((PointHistory) any());
    }

    @Test
    void usePoint_insufficient_throwsIllegalArgument() {
        Money amount = Money.of(1200);
        when(validationService.validate(amount, TransactionType.USE)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER));
        when(pointRepository.findById(USER_ID)).thenReturn(Optional.of(DUMMY_USER_POINT));

        assertThrows(IllegalArgumentException.class,
                () -> pointService.usePoint(USER_ID, amount));
    }
}
