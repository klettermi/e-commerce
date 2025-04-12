package kr.hhplus.be.server.domain.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    ;

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }
}