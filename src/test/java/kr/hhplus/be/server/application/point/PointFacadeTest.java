package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointFacadeTest {

    @Mock private PointService pointService;
    @InjectMocks private PointFacade pointFacade;

    private final Long userId = 42L;

    @BeforeEach
    void init() {
        // nothing yet
    }

    @Test
    void getPoint_delegatesAndMaps() {
        // given
        PointInput.GetPoint input = new PointInput.GetPoint();
        ReflectionTestUtils.setField(input, "userId", userId);

        PointInfo.UserPointInfo stubInfo = mock(PointInfo.UserPointInfo.class);
        when(stubInfo.getUserId()).thenReturn(userId);
        Money balance = Money.of(500);
        when(stubInfo.getBalance()).thenReturn(balance);

        when(pointService.getPoint(argThat(cmd -> cmd.getUserId().equals(userId))))
                .thenReturn(stubInfo);

        // when
        PointOutput.UserPoint output = pointFacade.getPoint(input);

        // then
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getBalance()).isSameAs(balance);

        verify(pointService).getPoint(any(PointCommand.GetPoint.class));
        verifyNoMoreInteractions(pointService);
    }

    @Test
    void getPointHistory_delegatesAndMaps() {
        // given
        PointInput.GetHistory input = new PointInput.GetHistory();
        ReflectionTestUtils.setField(input, "userId", userId);

        PointInfo.HistoryListInfo stubList = mock(PointInfo.HistoryListInfo.class);
        when(stubList.getUserId()).thenReturn(userId);

        PointInfo.HistoryItemInfo hi1 = mock(PointInfo.HistoryItemInfo.class);
        when(hi1.getId()).thenReturn(1L);
        when(hi1.getType()).thenReturn("CHARGE");
        Money amt1 = Money.of(100);
        when(hi1.getAmount()).thenReturn(amt1);

        PointInfo.HistoryItemInfo hi2 = mock(PointInfo.HistoryItemInfo.class);
        when(hi2.getId()).thenReturn(2L);
        when(hi2.getType()).thenReturn("USE");
        Money amt2 = Money.of(50);
        when(hi2.getAmount()).thenReturn(amt2);

        when(stubList.getHistory()).thenReturn(List.of(hi1, hi2));

        when(pointService.getPointHistory(argThat(cmd -> cmd.getUserId().equals(userId))))
                .thenReturn(stubList);

        // when
        PointOutput.HistoryList output = pointFacade.getPointHistory(input);

        // then
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getHistory()).hasSize(2);

        var out1 = output.getHistory().get(0);
        assertThat(out1.getId()).isEqualTo(1L);
        assertThat(out1.getType()).isEqualTo("CHARGE");
        assertThat(out1.getAmount()).isSameAs(amt1);

        var out2 = output.getHistory().get(1);
        assertThat(out2.getId()).isEqualTo(2L);
        assertThat(out2.getType()).isEqualTo("USE");
        assertThat(out2.getAmount()).isSameAs(amt2);

        verify(pointService).getPointHistory(any(PointCommand.GetHistory.class));
        verifyNoMoreInteractions(pointService);
    }

    @Test
    void charge_delegatesAndMaps() {
        // given
        int amount = 300;
        PointInput.Charge input = new PointInput.Charge();
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "amount", amount);

        PointInfo.UserPointInfo stubInfo = mock(PointInfo.UserPointInfo.class);
        when(stubInfo.getUserId()).thenReturn(userId);
        Money newBalance = Money.of(800);
        when(stubInfo.getBalance()).thenReturn(newBalance);

        when(pointService.chargePoint(argThat(cmd ->
                cmd.getUserId().equals(userId)
                        && cmd.getAmount().equals(Money.of(amount))
        ))).thenReturn(stubInfo);

        // when
        PointOutput.UserPoint output = pointFacade.charge(input);

        // then
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getBalance()).isSameAs(newBalance);

        verify(pointService).chargePoint(any(PointCommand.Charge.class));
        verifyNoMoreInteractions(pointService);
    }

    @Test
    void use_delegatesAndMaps() {
        // given
        int amount = 150;
        PointInput.Use input = new PointInput.Use();
        ReflectionTestUtils.setField(input, "userId", userId);
        ReflectionTestUtils.setField(input, "amount", amount);

        PointInfo.UserPointInfo stubInfo = mock(PointInfo.UserPointInfo.class);
        when(stubInfo.getUserId()).thenReturn(userId);
        Money newBalance = Money.of(650);
        when(stubInfo.getBalance()).thenReturn(newBalance);

        when(pointService.usePoint(argThat(cmd ->
                cmd.getUserId().equals(userId)
                        && cmd.getAmount().equals(Money.of(amount))
        ))).thenReturn(stubInfo);

        // when
        PointOutput.UserPoint output = pointFacade.use(input);

        // then
        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getBalance()).isSameAs(newBalance);

        verify(pointService).usePoint(any(PointCommand.Use.class));
        verifyNoMoreInteractions(pointService);
    }
}
