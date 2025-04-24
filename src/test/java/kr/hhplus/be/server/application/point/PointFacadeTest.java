package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointFacadeTest {

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointFacade pointFacade;

    private final long userId = 123L;
    private Money amount;
    private UserPoint userPoint;
    private List<PointHistory> historyList;

    @BeforeEach
    void setUp() {
        amount = Money.of(500);
        userPoint = new UserPoint();
        userPoint.setPointBalance(Money.of(1000));
        PointHistory h1 = PointHistory.createChargeHistory(null, amount);
        PointHistory h2 = PointHistory.createUseHistory(null, amount);
        historyList = List.of(h1, h2);
    }

    @Test
    void chargePoint_delegatesToService() {
        when(pointService.chargePoint(userId, amount)).thenReturn(userPoint);

        UserPoint result = pointFacade.chargePoint(userId, amount);

        assertThat(result).isSameAs(userPoint);
        verify(pointService).chargePoint(userId, amount);
    }

    @Test
    void getPointHistory_delegatesToService() {
        when(pointService.getPointHistory(userId)).thenReturn(historyList);

        List<PointHistory> result = pointFacade.getPointHistory(userId);

        assertThat(result).isSameAs(historyList);
        verify(pointService).getPointHistory(userId);
    }

    @Test
    void getPoint_delegatesToService() {
        when(pointService.getPoint(userId)).thenReturn(userPoint);

        UserPoint result = pointFacade.getPoint(userId);

        assertThat(result).isSameAs(userPoint);
        verify(pointService).getPoint(userId);
    }

    @Test
    void usePoint_delegatesToService() {
        when(pointService.usePoint(userId, amount)).thenReturn(userPoint);

        UserPoint result = pointFacade.usePoint(userId, amount);

        assertThat(result).isSameAs(userPoint);
        verify(pointService).usePoint(userId, amount);
    }
}
