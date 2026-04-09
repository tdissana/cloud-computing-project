package lk.watupa.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SortFieldValidator implements ConstraintValidator<ValidSortField, String> {

    private static final Set<String> VALID_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "basesalary",
            "totalcompensation",
            "seniority",
            "timestamp",
            "approvedat",
            "grossmonthlysalary",
            "yearsofexperience",
            "upvotes"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return VALID_SORT_FIELDS.contains(value.toLowerCase());
    }
}
