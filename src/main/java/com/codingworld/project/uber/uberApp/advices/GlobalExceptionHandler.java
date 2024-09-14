package com.codingworld.project.uber.uberApp.advices;

import com.codingworld.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.codingworld.project.uber.uberApp.exceptions.RuntimeConflictException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeConflictException(RuntimeConflictException ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(Exception ex) {
//        ApiError apiError = ApiError.builder()
//                .httpStatus(HttpStatus.FORBIDDEN)
//                .message(ex.getMessage())
//                .build();
//        return buildErrorResponseEntity(apiError);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationError(MethodArgumentNotValidException ex) {
        List<String> errors = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage()).collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Input Validation Failed")
                .subErrors(errors)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getHttpStatus());
    }
}
