package lk.watupa.search.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that currency is one of the allowed currency codes (case-insensitive).
 */
@Documented
@Constraint(validatedBy = CurrencyValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCurrency {
    String message() default "currency must be a valid ISO 4217 currency code (e.g., USD, LKR, EUR, GBP)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
