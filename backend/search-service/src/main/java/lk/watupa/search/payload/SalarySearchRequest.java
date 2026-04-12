package lk.watupa.search.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lk.watupa.search.validation.ValidSeniorityLevel;
import lk.watupa.search.validation.ValidSortDirection;
import lk.watupa.search.validation.ValidSortField;
import lk.watupa.search.validation.ValidVerificationStatus;

/**
 * Query parameters for salary search. All fields are optional.
 */
@Data
public class SalarySearchRequest {
    private String country;
    private String company;
    private String jobTitle;

    @ValidSeniorityLevel
    @JsonAlias("seniorityLevel")
    private String experienceLevel;

    private String employmentType;

    private String currency;

    @Min(0)
    @JsonAlias("minExperience")
    private Integer minSeniority;

    @Max(80)
    @JsonAlias("maxExperience")
    private Integer maxSeniority;

    @Min(0)
    private Integer page;

    @Min(1)
    @Max(100)
    private Integer size;

    @ValidSortField
    private String sortBy;

    @ValidSortDirection
    private String sortDir;

    /**
     * VERIFIED = approved submissions only; UNVERIFIED = not approved (e.g. pending/rejected).
     */
    @ValidVerificationStatus
    private String verificationStatus;
}
