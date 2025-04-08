package kr.hhplus.be.server.domain.cart.controller;


import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @GetMapping("/{userId}")
    public List<Map<String, Object>> getCart(@PathVariable Long userId) {
        return List.of(
                Map.of("productId", 10, "amount", 1)
        );
    }
}
