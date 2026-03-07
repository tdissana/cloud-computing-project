package lk.watupa.search.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a single validation error for a specific field.
 */
@Data
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String message;
}
