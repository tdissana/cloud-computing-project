package lk.watupa.stats.payload;

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
) {}
