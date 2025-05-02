package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponFacadeTest {

    @Mock private CouponService couponService;
    @InjectMocks private CouponFacade couponFacade;

    private final Long couponId = 100L;
    private final Long userId   = 200L;


    @Test
    void issueCoupon_delegatesToService_andMapsOutput() {
        // given
        CouponInput.Issue input = new CouponInput.Issue(couponId, userId);
        ReflectionTestUtils.setField(input, "couponId", couponId);
        ReflectionTestUtils.setField(input, "userId", userId);

        CouponInfo.IssuedCouponInfo info = mock(CouponInfo.IssuedCouponInfo.class);
        when(info.getId()).thenReturn(1L);
        when(info.getCouponId()).thenReturn(couponId);
        when(info.getUserId()).thenReturn(userId);
        when(info.getStatus()).thenReturn("ISSUED");

        when(couponService.issueCoupon(argThat(cmd ->
                cmd.getCouponId().equals(couponId) &&
                        cmd.getUserId().equals(userId)
        ))).thenReturn(info);

        // when
        CouponOutput.IssuedCoupon result = couponFacade.issueCoupon(input);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCouponId()).isEqualTo(couponId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo("ISSUED");

        verify(couponService).issueCoupon(any(CouponCommand.IssueCoupon.class));
        verifyNoMoreInteractions(couponService);
    }

    @Test
    void getCouponsByUserId_delegatesToService_andMapsOutputList() {
        // given
        CouponInput.GetByUser input = new CouponInput.GetByUser(userId);
        ReflectionTestUtils.setField(input, "userId", userId);

        CouponInfo.IssuedCouponList infoList = mock(CouponInfo.IssuedCouponList.class);
        when(infoList.getUserId()).thenReturn(userId);

        CouponInfo.IssuedCouponInfo info1 = mock(CouponInfo.IssuedCouponInfo.class);
        when(info1.getId()).thenReturn(10L);
        when(info1.getCouponId()).thenReturn(101L);
        when(info1.getUserId()).thenReturn(userId);
        when(info1.getStatus()).thenReturn("ACTIVE");

        CouponInfo.IssuedCouponInfo info2 = mock(CouponInfo.IssuedCouponInfo.class);
        when(info2.getId()).thenReturn(11L);
        when(info2.getCouponId()).thenReturn(102L);
        when(info2.getUserId()).thenReturn(userId);
        when(info2.getStatus()).thenReturn("EXPIRED");

        when(infoList.getCoupons()).thenReturn(List.of(info1, info2));

        when(couponService.getCouponsByUserId(argThat(cmd ->
                cmd.getUserId().equals(userId)
        ))).thenReturn(infoList);

        // when
        CouponOutput.IssuedCouponList result = couponFacade.getCouponsByUserId(input);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCoupons()).hasSize(2);

        var out1 = result.getCoupons().get(0);
        assertThat(out1.getId()).isEqualTo(10L);
        assertThat(out1.getCouponId()).isEqualTo(101L);
        assertThat(out1.getUserId()).isEqualTo(userId);
        assertThat(out1.getStatus()).isEqualTo("ACTIVE");

        var out2 = result.getCoupons().get(1);
        assertThat(out2.getId()).isEqualTo(11L);
        assertThat(out2.getCouponId()).isEqualTo(102L);
        assertThat(out2.getUserId()).isEqualTo(userId);
        assertThat(out2.getStatus()).isEqualTo("EXPIRED");

        verify(couponService).getCouponsByUserId(any(CouponCommand.GetCouponsByUser.class));
        verifyNoMoreInteractions(couponService);
    }
}
