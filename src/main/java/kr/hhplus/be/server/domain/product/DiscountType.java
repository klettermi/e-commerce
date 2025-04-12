package kr.hhplus.be.server.domain.product;

public enum DiscountType {
    NONE,    // 할인 없음
    FIXED,   // 정액 할인 (할인 금액을 뺌)
    PERCENT  // 정률 할인 (할인율을 적용)
}