package kr.hhplus.be.server.domain.cart.controller;

import kr.hhplus.be.server.domain.cart.dto.CartResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @GetMapping("/{userId}")
    public List<CartResponse> getCart(@PathVariable Long userId) {
        return List.of(
                new CartResponse(10L, "기계식 키보드", 2),
                new CartResponse(12L, "무선 마우스", 1)
        );
    }
}