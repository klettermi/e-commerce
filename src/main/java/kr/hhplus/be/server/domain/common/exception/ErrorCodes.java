package kr.hhplus.be.server.domain.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    CONCURRENCY_COMFLICT_NOT_RESOLVED("다시 시도했으나 동시성 충돌이 해결되지 않았습니다."),
    ;

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }
}