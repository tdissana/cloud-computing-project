package lk.watupa.search.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only entity mapped to the unified salary.submission table.
 * Search may return APPROVED (verified) or non-approved (unverified) rows; read-only.
 */
@Entity
@Table(name = "submission", schema = "salary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedSalary {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "seniority")
    private Integer seniority;

    @Column(name = "country")
    private String country;

    @Column(name = "currency")
    private String currency;

    @Column(name = "total_compensation")
    private Double totalCompensation;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "anonymize")
    private Boolean anonymize;

    @Column(name = "status")
    private String status;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
