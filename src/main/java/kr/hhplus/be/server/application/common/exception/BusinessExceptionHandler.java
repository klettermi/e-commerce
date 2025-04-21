package kr.hhplus.be.server.application.common.exception;

import kr.hhplus.be.server.domain.common.exception.ErrorCodes;
import lombok.Builder;
import lombok.Getter;

public class BusinessExceptionHandler extends RuntimeException {

    @Getter
    private final ErrorCodes errorCode;

    @Builder
    public BusinessExceptionHandler(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Builder
    public BusinessExceptionHandler(ErrorCodes errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BusinessExceptionHandler(ErrorCodes errorCodes, String s, StackTraceElement[] stackTrace) {
        super(s);
        this.errorCode = errorCodes;
        this.setStackTrace(stackTrace);
    }
}