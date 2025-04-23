package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.interfaces.api.pointhistory.PointHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponse> getPoints(@PathVariable long userId) {
        UserPoint userPoint = pointService.getPoint(userId);
        PointResponse pointResponse = PointResponse.from(userPoint);

        return ApiResponse.success(pointResponse);
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponse> chargePoints(@RequestBody ChargePointRequest dto) {
        UserPoint userPoint = pointService.chargePoint(dto.userId(), dto.amount());
        PointResponse pointResponse = PointResponse.from(userPoint);

        return ApiResponse.success(pointResponse);
    }

    @PostMapping("/use")
    public ApiResponse<PointResponse> usePoints(@RequestBody UsePointRequest dto) {
        UserPoint userPoint = pointService.usePoint(dto.userId(), dto.amount());
        PointResponse pointResponse = PointResponse.from(userPoint);

        return ApiResponse.success(pointResponse);
    }

    @GetMapping("/{userId}")
    public List<PointHistoryResponse> getHistory(@PathVariable Long userId) {
        List<PointHistory> pointHistoryList = pointService.getPointHistory(userId);

        return pointHistoryList.stream()
                .map(PointHistoryResponse::fromEntity)
                .toList();
    }
}
