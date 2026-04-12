package lk.watupa.salary_submission.payload;

import lk.watupa.salary_submission.model.Submission;

import java.time.LocalDateTime;

public record SubmissionDto(
        Long id,
        String companyName,
        String jobTitle,
        String experienceLevel,
        Integer seniority,
        String country,
        String currency,
        Double totalCompensation,
        Double baseSalary,
        String skills,
        Boolean anonymize,
        String status,
        LocalDateTime timestamp
) {
    public static SubmissionDto from(Submission s) {
        return new SubmissionDto(
                s.getId(),
                s.getCompanyName(),
                s.getJobTitle(),
                s.getExperienceLevel() != null ? s.getExperienceLevel().name() : null,
                s.getSeniority(),
                s.getCountry(),
                s.getCurrency(),
                s.getTotalCompensation(),
                s.getBaseSalary(),
                s.getSkills(),
                s.getAnonymize(),
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getTimestamp()
        );
    }
}
