package lk.watupa.salary_submission.payload;

import lk.watupa.salary_submission.model.Submission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionResponse {
    private Long id;
    private String companyName;
    private String jobTitle;
    private String experienceLevel;
    private Integer seniority;
    private String country;
    private String currency;
    private Double totalCompensation;
    private Double baseSalary;
    private String skills;
    private boolean anonymize;
    private String status;
    private LocalDateTime timestamp;

    public static SubmissionResponse from(Submission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .companyName(Boolean.TRUE.equals(s.getAnonymize()) ? null : s.getCompanyName())
                .jobTitle(s.getJobTitle())
                .experienceLevel(s.getExperienceLevel() != null ? s.getExperienceLevel().name() : null)
                .seniority(s.getSeniority())
                .country(s.getCountry())
                .currency(s.getCurrency())
                .totalCompensation(s.getTotalCompensation())
                .baseSalary(s.getBaseSalary())
                .skills(s.getSkills())
                .anonymize(Boolean.TRUE.equals(s.getAnonymize()))
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .timestamp(s.getTimestamp())
                .build();
    }
}
