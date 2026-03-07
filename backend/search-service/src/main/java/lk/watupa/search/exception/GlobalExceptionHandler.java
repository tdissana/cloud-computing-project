package lk.watupa.search.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotation on RequestBody.
     * Returns field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        List<ValidationError> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(new ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .message("Validation failed")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles type mismatch errors from query parameters (GET requests).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid query parameter: {} - {}", ex.getName(), ex.getMessage());

        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError(
                ex.getName(),
                "Invalid value: " + ex.getValue() + " (expected type: " + ex.getRequiredType().getSimpleName() + ")"
        ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .message("Invalid query parameter")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error in search-service", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .message("An internal error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}