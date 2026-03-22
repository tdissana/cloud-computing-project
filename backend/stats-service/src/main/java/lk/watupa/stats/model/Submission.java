package lk.watupa.stats.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "submission", schema = "salary")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    // Stored as STRING enum — kept as String here so LOWER() works in JPQL
    // and no enum dependency is needed in the read-only stats service
    @Column(name = "experience_level", length = 50)
    private String experienceLevel;

    @Column(name = "seniority")
    private Integer seniority;

    @Column(length = 100)
    private String country;

    @Column(length = 10)
    private String currency;

    @Column(name = "total_compensation")
    private Double totalCompensation;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column
    private Boolean anonymize;

    @Column(length = 20)
    private String status;

    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
}