package lk.watupa.stats.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
//@Table(name = "submissions", schema = "salary")
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "role")
    private String role;

    @Column(name = "company")
    private String company;

    @Column(name = "level")
    private String level;

    @Column(name = "country")
    private String country;

    @Column(name = "salary_amount")
    private Double salaryAmount;

    @Column(name = "status")
    private String status;  // "PENDING" or "APPROVED"

    @Column(name = "anonymize")
    private Boolean anonymize;
}