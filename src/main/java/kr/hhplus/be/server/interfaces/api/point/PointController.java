package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointInput;
import kr.hhplus.be.server.application.point.PointOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@Validated
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponse.UserPoint> getPoint(@PathVariable Long userId) {
        PointInput.GetPoint in = new PointInput.GetPoint();
        in.setUserId(userId);
        PointOutput.UserPoint out = pointFacade.getPoint(in);
        return ApiResponse.success(PointResponse.UserPoint.fromOutput(out));
    }

    @GetMapping("/{userId}/history")
    public ApiResponse<PointResponse.HistoryList> getPointHistory(@PathVariable Long userId) {
        PointInput.GetHistory in = new PointInput.GetHistory();
        in.setUserId(userId);
        PointOutput.HistoryList out = pointFacade.getPointHistory(in);
        return ApiResponse.success(PointResponse.HistoryList.fromOutput(out));
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponse.UserPoint> chargePoint(
            @Valid @RequestBody PointRequest.Charge request
    ) {
        PointOutput.UserPoint out = pointFacade.charge(request.toInput());
        return ApiResponse.success(PointResponse.UserPoint.fromOutput(out));
    }

    @PostMapping("/use")
    public ApiResponse<PointResponse.UserPoint> usePoint(
            @Valid @RequestBody PointRequest.Use request
    ) {
        PointOutput.UserPoint out = pointFacade.use(request.toInput());
        return ApiResponse.success(PointResponse.UserPoint.fromOutput(out));
    }
}
