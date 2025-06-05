package com.inkcloud.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    // 모든 예상 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception ex) {
        log.error("Unexpected error at [{} {}] : {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .code("500")
                .message("Internal Server Error: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 유효성 검증 실패 (DTO @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        log.error("Validation error at [{} {}]", request.getMethod(), request.getRequestURI());

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            builder.append("[")
                    .append(fieldError.getField())
                    .append("] : ")
                    .append(fieldError.getDefaultMessage())
                    .append(" (Provided: ")
                    .append(fieldError.getRejectedValue())
                    .append("), ");
        }

        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message(builder.toString())
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}