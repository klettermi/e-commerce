package kr.hhplus.be.server.interfaces.api.pointhistory;

import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points/history")
@RequiredArgsConstructor
public class PointHistoryController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public List<PointHistoryResponse> getHistory(@PathVariable Long userId) {
        List<PointHistory> pointHistoryList = pointService.getPointHistory(userId);
        List<PointHistoryResponse> responses = pointHistoryList.stream()
                .map(PointHistoryResponse::fromEntity)
                .toList();

        return responses;
    }
}