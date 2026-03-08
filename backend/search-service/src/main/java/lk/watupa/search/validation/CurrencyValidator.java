package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates currency field: accepts valid 3-letter ISO 4217 currency codes (case-insensitive).
 */
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    private static final Set<String> VALID_CURRENCIES = new HashSet<>(Arrays.asList(
            "usd", "eur", "gbp", "inr", "lkr", "pkr", "bdt", "thb", "idr", "php",
            "myr", "sgd", "jpy", "cad", "aud", "nzd", "cny", "aed", "sar", "qar"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null is valid (optional field)
        if (value == null) {
            return true;
        }
        return VALID_CURRENCIES.contains(value.toLowerCase());
    }
}
