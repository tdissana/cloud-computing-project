package lk.watupa.search_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read-only entity mapped to the approved_salaries view/table in the salary schema.
 * The search-service is read-only — it never writes or modifies salary data.
 */
@Entity
@Table(name = "approved_salaries", schema = "salary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedSalary {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "seniority_level")
    private String seniorityLevel;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "gross_monthly_salary")
    private BigDecimal grossMonthlySalary;

    @Column(name = "currency")
    private String currency;

    @Column(name = "additional_compensation")
    private BigDecimal additionalCompensation;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "years_at_company")
    private Integer yearsAtCompany;

    @Column(name = "tech_stack")
    private String techStack;

    /** When true, public-facing results hide company name and city. */
    @Column(name = "anonymized")
    private Boolean anonymized;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "upvotes")
    private Integer upvotes;

    @Column(name = "downvotes")
    private Integer downvotes;
}