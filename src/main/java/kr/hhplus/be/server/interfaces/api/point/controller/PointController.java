package kr.hhplus.be.server.interfaces.api.point.controller;

import kr.hhplus.be.server.application.common.dto.ApiResponse;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.interfaces.api.point.dto.ChargePointRequestDto;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponseDto;
import kr.hhplus.be.server.interfaces.api.point.dto.UsePointRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public ApiResponse<PointResponseDto> getPoints(@PathVariable long userId) {
        return ApiResponse.success(pointService.getPoint(userId));
    }

    @PostMapping("/charge")
    public ApiResponse<PointResponseDto> chargePoints(@RequestBody ChargePointRequestDto dto) {
        return ApiResponse.success(pointService.chargePoint(dto.userId(), dto.amount()));
    }

    @PostMapping("/use")
    public ApiResponse<PointResponseDto> usePoints(@RequestBody UsePointRequestDto dto) {
        return ApiResponse.success(pointService.usePoint(dto.userId(), dto.amount()));
    }
}
