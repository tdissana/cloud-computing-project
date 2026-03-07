package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates employmentType field: accepts valid employment types (case-insensitive).
 */
public class EmploymentTypeValidator implements ConstraintValidator<ValidEmploymentType, String> {

    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
            "full-time",
            "part-time",
            "contract",
            "freelance"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null is valid (optional field)
        if (value == null) {
            return true;
        }
        return VALID_TYPES.contains(value.toLowerCase());
    }
}
