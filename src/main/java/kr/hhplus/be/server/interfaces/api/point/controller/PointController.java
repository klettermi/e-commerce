package kr.hhplus.be.server.interfaces.api.point.controller;

import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.interfaces.api.point.dto.ChargePointRequestDto;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponseDto;
import kr.hhplus.be.server.interfaces.api.point.dto.UsePointRequestDto;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/{userId}")
    public PointResponseDto getPoints(@PathVariable long userId) {
        return pointService.getPoint(userId);
    }

    @PostMapping("/charge")
    public PointResponseDto chargePoints(@RequestBody ChargePointRequestDto dto) {
        return pointService.chargePoint(dto.userId(), dto.amount());
    }

    @PostMapping("/use")
    public PointResponseDto usePoints(@RequestBody UsePointRequestDto dto) {
        return pointService.usePoint(dto.userId(), dto.amount());
    }
}
