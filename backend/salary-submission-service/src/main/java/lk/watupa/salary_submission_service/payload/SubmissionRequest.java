package lk.watupa.salary_submission_service.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lk.watupa.salary_submission_service.enums.ExperienceLevel;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotBlank(message = "Company is required")
    @Size(max = 255)
    private String company;

    @NotBlank(message = "Role is required")
    @Size(max = 255)
    private String role;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @Size(max = 100)
    private String country = "Sri Lanka";

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Integer baseSalary;

    @Positive(message = "Total compensation must be positive")
    private Integer totalCompensation;

    @Size(max = 10)
    private String currency = "LKR";

    private boolean anonymize = true;
}
