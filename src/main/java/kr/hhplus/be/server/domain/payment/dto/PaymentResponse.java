package kr.hhplus.be.server.domain.payment.dto;

public record PaymentResponse(Long orderId, String status, int paidAmount) {}