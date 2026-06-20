package com.nmquan1503.backend_springboot.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException exception) {
        ResponseCode responseCode = exception.getResponseCode();
        ApiResponse<Object> response = ApiResponse.builder()
            .code(responseCode.getCode())
            .success(false)
            .message(responseCode.getMessage())
            .build();
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException exception) {
        ResponseCode responseCode = ResponseCode.UNAUTHORIZED;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();
        return ResponseEntity.status(403).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ResponseCode responseCode;
        try {
            responseCode = ResponseCode.valueOf(enumKey);
        }
        catch (IllegalArgumentException e) {
            responseCode = ResponseCode.INVALID_ENUM_KEY;
        }
        ApiResponse<Object> response = ApiResponse.error(responseCode);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ResponseCode responseCode = ResponseCode.TYPE_MISMATCH;
        ApiResponse<Object> response = ApiResponse.error(responseCode);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException exception) {
        System.out.println("exception name: " + exception.toString());
        System.out.println("exception message: " + exception.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ResponseCode.UNKNOWN_ERROR);
        return ResponseEntity.status(500).body(response);
    }
}
