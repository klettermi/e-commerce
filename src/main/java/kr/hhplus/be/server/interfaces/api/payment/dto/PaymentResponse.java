package kr.hhplus.be.server.interfaces.api.payment.dto;

public record PaymentResponse(Long orderId, String status, int paidAmount) {}