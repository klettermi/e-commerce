package kr.hhplus.be.server.domain.common.exception;

import lombok.Getter;

/**
 * 도메인 계층의 공통 예외 추상 클래스
 */
@Getter
public abstract class DomainException extends RuntimeException {
    private final String errorCode;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 요청한 엔티티를 찾지 못했을 때 던지는 예외
     */
    public static class EntityNotFoundException extends DomainException {
        public EntityNotFoundException(String message) {
            super("ENTITY_NOT_FOUND", message);
        }
    }

    /**
     * 중복된 엔티티가 존재할 때 던지는 예외
     */
    public static class DuplicateEntityException extends DomainException {
        public DuplicateEntityException(String message) {
            super("DUPLICATE_ENTITY", message);
        }
    }

    /**
     * 비즈니스 로직 수행이 불가능한 잘못된 상태일 때 던지는 예외
     */
    public static class InvalidStateException extends DomainException {
        public InvalidStateException(String message) {
            super("INVALID_STATE", message);
        }
    }
}

