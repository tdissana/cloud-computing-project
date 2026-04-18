package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SeniorityLevelValidator implements ConstraintValidator<ValidSeniorityLevel, String> {

    private static final Set<String> VALID_LEVELS = new HashSet<>(Arrays.asList(
            "junior", "mid", "senior", "lead", "principal", "manager"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return VALID_LEVELS.contains(value.toLowerCase());
    }
}
