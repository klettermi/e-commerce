package kr.hhplus.be.server.api.coupon.controller;


import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @GetMapping
    public List<Map<String, Object>> getCoupons(@RequestParam Long userId) {
        return List.of(
                Map.of("couponId", 3, "name", "10% 할인 쿠폰", "discountType", "PERCENT", "discountAmount", 10)
        );
    }

    @PostMapping("/issue")
    public Map<String, Object> issueCoupon(@RequestBody Map<String, Object> body) {
        Long couponId = Long.valueOf(String.valueOf(body.get("couponId")));
        if (couponId == 3) {
            return Map.of("message", "쿠폰 발급 성공", "issuedCouponId", 1005);
        } else {
            throw new RuntimeException("쿠폰 발급 실패");
        }
    }
}
