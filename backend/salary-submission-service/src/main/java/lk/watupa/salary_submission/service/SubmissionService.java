package lk.watupa.salary_submission.service;

import lk.watupa.salary_submission.enums.EmploymentType;
import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import lk.watupa.salary_submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public Long submit(
            String companyName,
            String jobTitle,
            ExperienceLevel experienceLevel,
            EmploymentType employmentType,
            Integer seniority,
            String country,
            String currency,
            Double totalCompensation,
            Double baseSalary,
            String skills,
            boolean anonymize
    ) {
        Submission submission = Submission.builder()
                .companyName(companyName)
                .jobTitle(jobTitle)
                .experienceLevel(experienceLevel)
                .employmentType(employmentType)
                .seniority(seniority)
                .country(country)
                .currency(currency)
                .totalCompensation(totalCompensation)
                .baseSalary(baseSalary)
                .skills(skills)
                .anonymize(anonymize)
                .status(Status.PENDING)
                .build();

        submissionRepository.save(submission);
        return submission.getId();
    }
}
