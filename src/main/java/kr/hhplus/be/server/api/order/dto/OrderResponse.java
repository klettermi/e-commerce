package kr.hhplus.be.server.api.order.dto;

public record OrderResponse(Long id, String orderNo, int amount, String status) {}