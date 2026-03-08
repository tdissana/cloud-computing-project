package lk.watupa.search.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that seniorityLevel is one of the allowed levels (case-insensitive).
 */
@Documented
@Constraint(validatedBy = SeniorityLevelValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSeniorityLevel {
    String message() default "seniorityLevel must be one of: Junior, Mid, Senior, Lead, Manager (case-insensitive)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
