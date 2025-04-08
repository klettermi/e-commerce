package kr.hhplus.be.server.api.point.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @GetMapping("/{userId}")
    public Map<String, Object> getPoint(@PathVariable Long userId) {
        return Map.of("userId", userId, "balance", 50000);
    }

    @PostMapping("/charge")
    public Map<String, Object> charge(@RequestBody Map<String, Object> body) {
        return Map.of("userId", body.get("userId"), "newBalance", 60000);
    }
}
