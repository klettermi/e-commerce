package kr.hhplus.be.server.interfaces.api.point_history.controller;

import kr.hhplus.be.server.interfaces.api.point_history.dto.PointHistoryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points/history")
public class PointHistoryController {

    @GetMapping("/{userId}")
    public List<PointHistoryResponse> getHistory(@PathVariable Long userId) {
        return List.of(
                new PointHistoryResponse(userId, 10000, "CHARGE"),
                new PointHistoryResponse(userId, -5000, "USE")
        );
    }
}