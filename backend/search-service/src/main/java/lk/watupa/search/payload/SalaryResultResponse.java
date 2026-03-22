package lk.watupa.search.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public-facing salary result aligned with the frontend search UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryResultResponse {
    private String id;
    private String companyName;
    private String jobTitle;
    private String seniorityLevel;
    private String employmentType;
    private String country;
    private String city;
    private Double grossMonthlySalary;
    private String currency;
    private Double additionalCompensation;
    private Integer yearsOfExperience;
    private String techStack;
    private Boolean anonymized;
    private String approvedAt;
    private int upvotes;
    private int downvotes;
    private String status;  // APPROVED, PENDING, REJECTED
}
