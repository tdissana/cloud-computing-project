package lk.watupa.salary_submission_service.payload;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lk.watupa.salary_submission_service.enums.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "submissions", schema = "salary")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submission_id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "company",nullable = false)
    private String company;

    @NotBlank
    @Size(max = 100)
    @Column(name = "role", nullable = false)
    private String role;

    @NotBlank
    @Size(max = 50)
    @Column(name = "experience_level", nullable = false)
    private String experienceLevel;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "total_compensation", nullable = false)
    private BigDecimal totalCompensation;

    @Column(name = "anonymize",nullable = false)
    private boolean anonymize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private Status status;

}
