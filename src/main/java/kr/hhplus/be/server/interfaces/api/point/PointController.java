package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponse> getPoints(@PathVariable long userId) {
        return ApiResponse.success(pointService.getPoint(userId));
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponse> chargePoints(@RequestBody ChargePointRequest dto) {
        return ApiResponse.success(pointService.chargePoint(dto.userId(), dto.amount()));
    }

    @PostMapping("/use")
    public ApiResponse<PointResponse> usePoints(@RequestBody UsePointRequest dto) {
        return ApiResponse.success(pointService.usePoint(dto.userId(), dto.amount()));
    }
}
