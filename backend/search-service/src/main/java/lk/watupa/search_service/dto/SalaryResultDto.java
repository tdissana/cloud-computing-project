package lk.watupa.search_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Public-facing salary result.
 * When a record is anonymized, companyName and city are masked before returning.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResultDto {
    private UUID id;
    private String companyName;    // masked to "Anonymous" if anonymized=true
    private String jobTitle;
    private String seniorityLevel;
    private String employmentType;
    private String country;
    private String city;           // masked to null if anonymized=true
    private BigDecimal grossMonthlySalary;
    private String currency;
    private BigDecimal additionalCompensation;
    private Integer yearsOfExperience;
    private String techStack;
    private Boolean anonymized;
    private LocalDateTime approvedAt;
    private Integer upvotes;
    private Integer downvotes;
}