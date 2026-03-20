package lk.watupa.search.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure for all API errors.
 * Includes field-level validation errors when applicable.
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationError> errors;
}
