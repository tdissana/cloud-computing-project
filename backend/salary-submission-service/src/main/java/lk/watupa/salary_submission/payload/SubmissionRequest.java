package lk.watupa.salary_submission.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lk.watupa.salary_submission.enums.EmploymentType;
import lk.watupa.salary_submission.enums.ExperienceLevel;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 255)
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Size(max = 255)
    private String jobTitle;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    /** Years of experience (seniority). */
    private Integer seniority;

    @Size(max = 100)
    private String country = "Sri Lanka";

    @Size(max = 10)
    private String currency = "LKR";

    @NotNull(message = "Total compensation is required")
    @Positive(message = "Total compensation must be positive")
    private Double totalCompensation;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Double baseSalary;

    private String skills;

    private boolean anonymize = true;
}
