package kr.hhplus.be.server.interfaces.advice;

import kr.hhplus.be.server.application.common.ApiResponse;
import kr.hhplus.be.server.application.common.ErrorResponse;
import kr.hhplus.be.server.domain.common.exception.ApplicationException;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.common.exception.ErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainExceptions.DomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleDomainException(DomainExceptions.DomainException ex) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        ApiResponse<Object> response = ApiResponse.failure(error);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleApplicationException(ApplicationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        ApiResponse<Object> response = ApiResponse.failure(error);
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        // 로깅 등을 통해 상세 오류를 확인한 후 기본 응답 처리
        ErrorResponse error = new ErrorResponse(ErrorCodes.INTERNAL_SERVER_ERROR.getCode(), "예기치 않은 오류가 발생했습니다.");
        ApiResponse<Object> response = ApiResponse.failure(error);
        return ResponseEntity.status(500).body(response);
    }

}