package kr.hhplus.be.server.domain.coupon.controller;

import kr.hhplus.be.server.domain.coupon.dto.CouponResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @GetMapping
    public List<CouponResponse> getCoupons(@RequestParam Long userId) {
        return List.of(new CouponResponse(1L, "WELCOME10", "PERCENT", 10));
    }
}