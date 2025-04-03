package kr.hhplus.be.server.domain.point.controller;

import kr.hhplus.be.server.domain.point.dto.PointResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @GetMapping("/{userId}")
    public PointResponse getPoint(@PathVariable Long userId) {
        return new PointResponse(userId, 100000);
    }
}
