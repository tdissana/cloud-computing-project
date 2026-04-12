package lk.watupa.salary_submission.payload;

public record SubmissionSearchRequest(
        String country,
        String company,
        String jobTitle,
        String experienceLevel,
        String currency,
        Integer minSeniority,
        Integer maxSeniority,
        String verificationStatus,
        Integer page,
        Integer size,
        String sortBy,
        String sortDir
) {}
