package com.configcenter.backend.common.exception;

import com.configcenter.backend.common.api.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<ErrorResponseView>> handleBizException(BizException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.failure(exception.getCode(), exception.getMessage(),
                        new ErrorResponseView(exception.getDetails())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponseView>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("VALIDATION_ERROR", "Request validation failed",
                        new ErrorResponseView(exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetail("FIELD_ERROR", error.getField(), error.getDefaultMessage()))
                .toList())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponseView>> handleUnexpectedException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("INTERNAL_SERVER_ERROR", exception.getMessage(),
                        new ErrorResponseView(List.of())));
    }
}
