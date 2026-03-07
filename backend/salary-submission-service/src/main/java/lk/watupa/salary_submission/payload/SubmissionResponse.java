package lk.watupa.salary_submission.payload;

import lk.watupa.salary_submission.model.Submission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SubmissionResponse {
    private UUID id;
    private String company;
    private String role;
    private String experienceLevel;
    private String country;
    private double baseSalary;
    private double totalCompensation;
    private String  currency;
    private boolean anonymize;
    private String  status;
    private LocalDateTime createdAt;

    public static SubmissionResponse from(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .company(submission.getAnonymize() ? null : submission.getCompany())
                .role(submission.getRole())
                .experienceLevel(submission.getExperienceLevel() != null ? submission.getExperienceLevel().name() : null)
                .country(submission.getCountry())
                .baseSalary(submission.getBaseSalary())
                .totalCompensation(submission.getTotalCompensation())
                .currency(submission.getCurrency())
                .anonymize(Boolean.TRUE.equals(submission.getAnonymize()))
                .status(submission.getStatus() != null ? submission.getStatus().name() : null)
                .createdAt(submission.getCreatedAt())
                .build();
    }
}
