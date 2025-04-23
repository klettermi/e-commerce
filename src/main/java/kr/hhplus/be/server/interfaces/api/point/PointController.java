package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponse> getPoints(@PathVariable long userId) {
        UserPoint userPoint = pointService.getPoint(userId);
        PointResponse pointResponse = PointResponse.fromEntity(userPoint);
        return ApiResponse.success(pointResponse);
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponse> chargePoints(@RequestBody ChargePointRequest dto) {
        UserPoint userPoint = pointService.chargePoint(dto.userId(), dto.amount());
        PointResponse pointResponse = PointResponse.fromEntity(userPoint);
        return ApiResponse.success(pointResponse);
    }

    @PostMapping("/use")
    public ApiResponse<PointResponse> usePoints(@RequestBody UsePointRequest dto) {
        UserPoint userPoint = pointService.usePoint(dto.userId(), dto.amount());
        PointResponse pointResponse = PointResponse.fromEntity(userPoint);
        return ApiResponse.success(pointResponse);
    }
}
