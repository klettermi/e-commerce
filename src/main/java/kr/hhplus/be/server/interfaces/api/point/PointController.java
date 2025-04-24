package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponse> getPoints(@PathVariable long userId) {
        UserPoint userPoint = pointFacade.getPoint(userId);
        PointResponse pointResponse = PointResponse.fromEntity(userPoint);
        return ApiResponse.success(pointResponse);
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponse> chargePoints(@RequestBody ChargePointRequest dto) {
        UserPoint userPoint = pointFacade.chargePoint(dto.userId(), dto.amount());
        PointResponse pointResponse = PointResponse.fromEntity(userPoint);
        return ApiResponse.success(pointResponse);
    }

    @GetMapping("/{userId}/history")
    public List<PointHistoryResponse> getHistory(@PathVariable Long userId) {
        List<PointHistory> pointHistoryList =  pointFacade.getPointHistory(userId);
        return pointHistoryList.stream()
                .map(PointHistoryResponse::fromEntity)
                .toList();
    }
}
