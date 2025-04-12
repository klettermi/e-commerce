package kr.hhplus.be.server.domain.common.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final String errorCode;

    public ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}