package kr.hhplus.be.server.domain.common;

public class Exceptions {

    // 도메인 계층의 기본 예외
    public static class DomainException extends RuntimeException {
        public DomainException(String message) {
            super(message);
        }
        public DomainException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 엔티티를 찾지 못했을 때 발생하는 예외
    public static class EntityNotFoundException extends DomainException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    // 중복된 엔티티가 발견되었을 때 발생하는 예외
    public static class DuplicateEntityException extends DomainException {
        public DuplicateEntityException(String message) {
            super(message);
        }
    }

    // 비즈니스 로직의 잘못된 상태나 연산이 발생했을 때 사용하는 예외
    public static class InvalidStateException extends DomainException {
        public InvalidStateException(String message) {
            super(message);
        }
    }
}