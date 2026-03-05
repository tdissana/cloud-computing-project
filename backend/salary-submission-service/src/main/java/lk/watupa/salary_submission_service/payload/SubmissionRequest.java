package lk.watupa.salary_submission_service.payload;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmissionRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String company;

    @NotBlank(message = "Role is required")
    @Size(max = 100, message = "Role cannot exceed 100 characters")
    private String role;

    @NotBlank(message = "Experience level is required")
    @Size(max = 50, message = "Experience level cannot exceed 50 characters")
    private String experienceLevel;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
    private BigDecimal baseSalary;

    @NotNull(message = "Total compensation is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total compensation must be greater than 0")
    private BigDecimal totalCompensation;

    private boolean anonymize;
}
