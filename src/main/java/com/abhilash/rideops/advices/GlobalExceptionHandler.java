package com.abhilash.rideops.advices;

import com.abhilash.rideops.exceptions.ExternalServiceException;
import com.abhilash.rideops.exceptions.InvalidRideOtpException;
import com.abhilash.rideops.exceptions.MissingRefreshTokenException;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiErrors> handleResourceNotFound(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(RuntimeConflictException.class)
    public ResponseEntity<ApiErrors> handleConflict(
            RuntimeConflictException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConcurrencyFailureException.class,
            OptimisticLockException.class
    })
    public ResponseEntity<ApiErrors> handlePersistenceConflict(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        log.warn("Persistence conflict: method={}, path={}, exception={}",
                request.getMethod(), request.getRequestURI(), exception.getClass().getSimpleName());
        return buildResponse(
                HttpStatus.CONFLICT,
                "The request conflicts with the current stored data",
                List.of(),
                request
        );
    }

    @ExceptionHandler(InvalidRideOtpException.class)
    public ResponseEntity<ApiErrors> handleInvalidRideOtp(
            InvalidRideOtpException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<ApiErrors> handleMissingRefreshToken(
            MissingRefreshTokenException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity<ApiErrors> handleAuthenticationFailure(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        log.debug("Authentication rejected: method={}, path={}, exception={}",
                request.getMethod(), request.getRequestURI(), exception.getClass().getSimpleName());
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication credentials are invalid or expired",
                List.of(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrors> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> errors = new ArrayList<>();
        errors.addAll(exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList());
        errors.addAll(exception.getBindingResult().getGlobalErrors().stream()
                .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                .toList());
        return buildResponse(HttpStatus.BAD_REQUEST, "Request validation failed", errors, request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrors> handleBindException(
            BindException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Request binding failed", errors, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrors> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Request validation failed", errors, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrors> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String requiredType = exception.getRequiredType() == null
                ? "the required type"
                : exception.getRequiredType().getSimpleName();
        String message = "Parameter '" + exception.getName() + "' must be a valid " + requiredType;
        return buildResponse(HttpStatus.BAD_REQUEST, message, List.of(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrors> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        String message = "Required request parameter '" + exception.getParameterName() + "' is missing";
        return buildResponse(HttpStatus.BAD_REQUEST, message, List.of(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrors> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or malformed",
                List.of(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrors> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrors> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrors> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrors> handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, "The requested endpoint was not found", List.of(), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrors> handleExternalServiceFailure(
            ExternalServiceException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrors> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unhandled request failure: method={}, path={}",
                request.getMethod(), request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of(),
                request
        );
    }

    private String formatFieldError(FieldError error) {
        String message = error.getDefaultMessage() == null ? "is invalid" : error.getDefaultMessage();
        return error.getField() + ": " + message;
    }

    private ResponseEntity<ApiErrors> buildResponse(
            HttpStatus status,
            String message,
            List<String> errors,
            HttpServletRequest request
    ) {
        ApiErrors apiErrors = ApiErrors.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(apiErrors);
    }
}
