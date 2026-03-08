package lk.watupa.search.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that sortDir is either "asc" or "desc" (case-insensitive).
 */
@Documented
@Constraint(validatedBy = SortDirectionValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortDirection {
    String message() default "sortDir must be 'asc' or 'desc' (case-insensitive)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
