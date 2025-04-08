package kr.hhplus.be.server.domain.order.dto;

public record OrderResponse(Long id, String orderNo, int amount, String status) {}