package kr.hhplus.be.server.interfaces.api.coupon;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponFacade couponFacade;

    @GetMapping
    public ApiResponse<List<CouponResponse>> getCoupons(@RequestParam Long userId) {
        List<IssuedCoupon> couponList = couponFacade.getCouponsByUserId(userId);
        List<CouponResponse> responseList = couponList.stream()
                .map(CouponResponse::fromIssuedCoupon)
                .toList();
        return ApiResponse.success(responseList);
    }

    @PostMapping("/issue")
    public ApiResponse<CouponResponse> issueCoupon(@RequestBody IssuedCouponRequest request) {
        IssuedCoupon issuedCoupon = couponFacade.issueCoupon(request.couponId(), request.userId());
        CouponResponse response = CouponResponse.fromIssuedCoupon(issuedCoupon);
        return ApiResponse.success(response);
    }
}
