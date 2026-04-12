package lk.watupa.salary_submission.enums;

import java.util.Arrays;

public enum EmploymentType {
    FullTime,
    PartTime,
    Contract,
    Freelance;

    public static EmploymentType from(String value) {
        if (value == null) return null;

        String normalized = value
                .replace("-", "")
                .replace(" ", "")
                .toLowerCase();

        return Arrays.stream(values())
                .filter(e -> e.name().toLowerCase().equals(normalized))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid EmploymentType: " + value)
                );
    }
}
