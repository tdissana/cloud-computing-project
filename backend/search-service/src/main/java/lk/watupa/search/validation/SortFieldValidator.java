package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates sortBy field: accepts valid sort field names (case-insensitive).
 */
public class SortFieldValidator implements ConstraintValidator<ValidSortField, String> {

    private static final Set<String> VALID_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "grossmonthlysalary",
            "yearsofexperience",
            "approvedat",
            "upvotes"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null is valid (optional field)
        if (value == null) {
            return true;
        }
        return VALID_SORT_FIELDS.contains(value.toLowerCase());
    }
}
