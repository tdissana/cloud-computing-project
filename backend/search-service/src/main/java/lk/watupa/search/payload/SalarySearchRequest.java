package lk.watupa.search.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lk.watupa.search.validation.*;

/**
 * Query parameters for salary search. All fields are optional — omit to skip that filter.
 * All enum-like fields accept case-insensitive values (e.g., "Mid" = "mid" = "MID").
 */
@Data
public class SalarySearchRequest {
    private String country;
    private String company;
    private String jobTitle;

    @ValidSeniorityLevel
    private String seniorityLevel;

    @ValidEmploymentType
    private String employmentType;

    @ValidCurrency
    private String currency;

    @Min(0)
    private Integer minExperience;

    @Max(80)
    private Integer maxExperience;

    @Min(0)
    private Integer page;

    @Min(1)
    @Max(100)
    private Integer size;

    @ValidSortField
    private String sortBy;   // e.g. "grossMonthlySalary", "approvedAt"

    @ValidSortDirection
    private String sortDir;  // "asc" or "desc"
}