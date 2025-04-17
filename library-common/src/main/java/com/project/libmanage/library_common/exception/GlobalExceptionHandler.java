package com.project.libmanage.library_common.exception;


import com.project.libmanage.library_common.constant.ErrorCode;
import com.project.libmanage.library_common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for REST controllers, providing consistent error responses.
 * Catches various exceptions and returns standardized {@link ApiResponse} objects in JSON format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles generic runtime exceptions not explicitly caught elsewhere.
     *
     * @param runtimeException the {@link RuntimeException} to handle
     * @return a {@link ResponseEntity} with 400 status and {@link ApiResponse} containing error details
     * @implNote Returns UNCATEGORIZED_EXCEPTION with the exception's message.
     */
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Void>> handlingRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(runtimeException.getMessage())
                .build());
    }

    /**
     * Handles custom application-specific exceptions.
     *
     * @param appException the {@link AppException} with predefined {@link ErrorCode}
     * @return a {@link ResponseEntity} with status from {@link ErrorCode} and error details
     * @implNote Logs the error and uses the error code's status and message.
     */
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException appException) {
        ErrorCode errorCode = appException.getErrorCode();
        log.error("Handling AppException: Code={}, Message={}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    /**
     * Handles access denied exceptions from Spring Security.
     *
     * @param accessDeniedException the {@link AccessDeniedException} indicating insufficient permissions
     * @return a {@link ResponseEntity} with 403 status and UNAUTHORIZED error details
     * @implNote Uses UNAUTHORIZED code (likely intended as 403 Forbidden).
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException accessDeniedException) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    /**
     * Handles validation errors from request body or parameters.
     *
     * @param ex the {@link MethodArgumentNotValidException} containing validation errors
     * @return a {@link ApiResponse  * @implNote Logs field and global errors, maps first error message to an {@link ErrorCode}.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingValidation(MethodArgumentNotValidException ex) {
        log.error("Field Errors: {}", ex.getBindingResult().getFieldErrors());
        log.error("Global Errors: {}", ex.getBindingResult().getGlobalErrors());

        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(fieldError.getDefaultMessage());
        }
        for (ObjectError globalError : ex.getBindingResult().getGlobalErrors()) {
            errorMessages.add(globalError.getDefaultMessage());
        }

        String errorMessage = errorMessages.isEmpty() ? "UNCATEGORIZED_EXCEPTION" : errorMessages.get(0);
        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(errorMessage);
        } catch (IllegalArgumentException e) {
            errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    /**
     * Handles authentication service exceptions from Spring Security.
     *
     * @param ex the {@link AuthenticationServiceException} indicating authentication failure
     * @return a {@link ResponseEntity} with LOGIN_ERROR status and custom message
     * @implNote Logs the error and appends exception message to response.
     */
    @ExceptionHandler(value = AuthenticationServiceException.class)
    ResponseEntity<ApiResponse<Void>> handlingAuthenticationException(AuthenticationServiceException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(ErrorCode.LOGIN_ERROR.getStatusCode()).body(ApiResponse.<Void>builder()
                .code(ErrorCode.LOGIN_ERROR.getCode())
                .message("Authentication failed: " + ex.getMessage())
                .build());
    }

    /**
     * Handles illegal argument exceptions, typically from invalid method arguments.
     *
     * @param ex the {@link IllegalArgumentException} to handle
     * @return a {@link ResponseEntity} with 400 status and exception message
     * @implNote Uses raw HTTP status code and exception message directly.
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handlingIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    /**
     * Handles null pointer exceptions indicating unexpected system errors.
     *
     * @param ex the {@link NullPointerException} to handle
     * @return a {@link ResponseEntity} with 500 status and error details
     * @implNote Uses generic 500 code with a prefixed message.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error system: " + ex.getMessage())
                .result(null)
                .build());
    }

    /**
     * Handles parse exceptions, typically from malformed JWT tokens.
     *
     * @param ex the {@link ParseException} indicating parsing failure
     * @return a {@link ResponseEntity} with 400 status and custom message
     * @implNote Logs the error and returns a token-specific error response.
     */
    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse<Void>> handlingParseException(ParseException ex) {
        log.error("Failed to parse JWT token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Invalid token format: " + ex.getMessage())
                .build());
    }
}