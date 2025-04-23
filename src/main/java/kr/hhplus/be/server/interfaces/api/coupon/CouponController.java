package kr.hhplus.be.server.interfaces.api.coupon;


import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<CouponResponse>> getCoupons(@RequestParam Long userId) {
        List<IssuedCoupon> couponList = couponService.getCouponsByUserId(userId);
        List<CouponResponse> responseList = couponList.stream()
                .map(CouponResponse::fromIssuedCoupon)
                .toList();
        return ApiResponse.success(responseList);
    }

    @PostMapping("/issue")
    public ApiResponse<CouponResponse> issueCoupon(@RequestBody IssuedCouponRequest request) {
        IssuedCoupon issuedCoupon = couponService.issueCoupon(request.couponId(), request.userId());
        CouponResponse response = CouponResponse.fromIssuedCoupon(issuedCoupon);
        return ApiResponse.success(response);
    }
}
