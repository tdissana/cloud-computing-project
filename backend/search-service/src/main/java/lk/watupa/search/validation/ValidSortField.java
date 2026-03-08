package lk.watupa.search.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that sortBy is one of the allowed sort fields (case-insensitive).
 */
@Documented
@Constraint(validatedBy = SortFieldValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortField {
    String message() default "sortBy must be one of: grossMonthlySalary, yearsOfExperience, approvedAt, upvotes (case-insensitive)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
