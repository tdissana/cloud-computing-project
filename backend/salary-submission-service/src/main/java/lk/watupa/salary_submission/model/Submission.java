package lk.watupa.salary_submission.model;

import jakarta.persistence.*;
import lk.watupa.salary_submission.enums.EmploymentType;
import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(schema = "salary", name = "submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 50)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 50)
    private EmploymentType employmentType;

    @Column(name = "seniority")
    private Integer seniority;

    @Column(length = 100)
    @Builder.Default
    private String country = "Sri Lanka";

    @Column(length = 10)
    @Builder.Default
    private String currency = "LKR";

    @Column(name = "total_compensation")
    private Double totalCompensation;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column
    @Builder.Default
    private Boolean anonymize = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
}
