package lk.watupa.salary_submission.service;

import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import lk.watupa.salary_submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public UUID submit(
            String company,
            String role,
            ExperienceLevel experienceLevel,
            String country,
            double baseSalary,
            double totalCompensation,
            String currency,
            boolean anonymize
            )
    {
        Submission submission = Submission.builder()
                .company(company)
                .role(role)
                .experienceLevel(experienceLevel)
                .country(country)
                .baseSalary(baseSalary)
                .totalCompensation(totalCompensation)
                .currency(currency)
                .anonymize(anonymize)
                .status(Status.PENDING)
                .build();

        submissionRepository.save(submission);
        return submission.getId();
    }
}
