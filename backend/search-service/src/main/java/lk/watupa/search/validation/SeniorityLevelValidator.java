package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates seniorityLevel field: accepts valid seniority levels (case-insensitive).
 */
public class SeniorityLevelValidator implements ConstraintValidator<ValidSeniorityLevel, String> {

    private static final Set<String> VALID_LEVELS = new HashSet<>(Arrays.asList(
            "junior",
            "mid",
            "senior",
            "lead",
            "manager"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null is valid (optional field)
        if (value == null) {
            return true;
        }
        return VALID_LEVELS.contains(value.toLowerCase());
    }
}
