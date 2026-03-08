package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates sortDir field: accepts "asc" or "desc" (case-insensitive).
 */
public class SortDirectionValidator implements ConstraintValidator<ValidSortDirection, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null is valid (optional field)
        if (value == null) {
            return true;
        }
        return value.equalsIgnoreCase("asc") || value.equalsIgnoreCase("desc");
    }
}
