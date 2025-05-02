package kr.hhplus.be.server.interfaces.api.coupon;


import jakarta.validation.Valid;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.CouponOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@Validated
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    /** 사용자별 쿠폰 조회 */
    @GetMapping
    public ApiResponse<List<CouponResponse>> getCoupons(
            @ModelAttribute @Valid CouponRequest.ListByUser request
    ) {
        CouponOutput.IssuedCouponList list = couponFacade.getCouponsByUserId(request.toInput());
        List<CouponResponse> responses = list.getCoupons().stream()
                .map(CouponResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    /** 쿠폰 발급 */
    @PostMapping("/issue")
    public ApiResponse<CouponResponse> issueCoupon(
            @RequestBody @Valid CouponRequest.Issue request
    ) {
        CouponOutput.IssuedCoupon issued = couponFacade.issueCoupon(request.toInput());
        return ApiResponse.success(CouponResponse.from(issued));
    }
}
