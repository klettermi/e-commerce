package kr.hhplus.be.server.interfaces.api.pointhistory;

public record PointHistoryResponse(Long userId, int amount, String type) {}