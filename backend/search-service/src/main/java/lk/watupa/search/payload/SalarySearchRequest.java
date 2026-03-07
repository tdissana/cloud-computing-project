package lk.watupa.search.payload;

import lombok.Data;

/**
 * Query parameters for salary search. All fields are optional — omit to skip that filter.
 */
@Data
public class SalarySearchRequest {
    private String country;
    private String company;
    private String jobTitle;
    private String seniorityLevel;
    private String employmentType;
    private String currency;
    private Integer minExperience;
    private Integer maxExperience;
    private Integer page;
    private Integer size;
    private String sortBy;   // e.g. "grossMonthlySalary", "approvedAt"
    private String sortDir;  // "asc" or "desc"
}