package org.atmsonline.sms.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ==========================================
    // BUSINESS LOGIC / ARGUMENT EXCEPTIONS
    // ==========================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setTitle("Invalid Request Argument");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Resource State Conflict");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/state-conflict"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // ==========================================
    // SECURITY / ACCESS CONTROL EXCEPTIONS
    // ==========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource."
        );
        problemDetail.setTitle("Access Denied");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/access-denied"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed: " + ex.getMessage()
        );
        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/unauthorized"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // ==========================================
    // BEAN VALIDATION (@Valid) EXCEPTIONS
    // ==========================================

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more input parameters."
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", validationErrors);

        return new ResponseEntity<>(problemDetail, headers, status);
    }

    // ==========================================
    // FALLTHROUGH / UNCHECKED EXCEPTIONS
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(
            Exception ex,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred."
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://atmsonline.org/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}