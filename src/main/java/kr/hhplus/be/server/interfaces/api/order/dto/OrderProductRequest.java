package kr.hhplus.be.server.interfaces.api.order.dto;

public record OrderProductRequest(
        Long productId,
        int quantity,
        int unitPoiont
) {
}
