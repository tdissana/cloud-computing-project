package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class VerificationStatusValidator implements ConstraintValidator<ValidVerificationStatus, String> {

    private static final Set<String> ALLOWED = Set.of("VERIFIED", "UNVERIFIED", "BOTH");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return ALLOWED.contains(value);
    }
}
