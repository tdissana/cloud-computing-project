package lk.watupa.bff.exception;

import lk.watupa.bff.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.stream.Collectors;

/**
 * Centralised error handling for the BFF.
 * Converts all exceptions into the uniform ApiResponse envelope
 * so the frontend always receives a consistent JSON shape.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Downstream service returned an error ──────────────────────────────────
    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<ApiResponse<Void>> handleDownstream(DownstreamException ex) {
        log.warn("Downstream error [{}]: {}", ex.getStatus(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.fail(ex.getMessage()));
    }

    // ── Bean Validation failures ──────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.debug("Validation error: {}", msg);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(msg));
    }

    // ── Cannot reach downstream service (network / DNS) ───────────────────────
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleNetworkError(ResourceAccessException ex) {
        log.error("Cannot reach downstream service: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("A downstream service is currently unavailable. Please try again later."));
    }

    // ── Catch-all ─────────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("An unexpected error occurred."));
    }
}
