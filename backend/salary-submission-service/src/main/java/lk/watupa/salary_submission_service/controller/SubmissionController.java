package lk.watupa.salary_submission_service.controller;

import jakarta.validation.Valid;
import lk.watupa.salary_submission_service.enums.Status;


import lk.watupa.salary_submission_service.model.Submission;

import lk.watupa.salary_submission_service.payload.SubmissionRequest;
import lk.watupa.salary_submission_service.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionRepository submissionRepository;
    @PostMapping
    public ResponseEntity<?> submit(@Valid @RequestBody SubmissionRequest submissionRequest) {
        Submission submission = Submission.builder()
                .company(submissionRequest.getCompany())
                .role(submissionRequest.getRole())
                .experienceLevel(submissionRequest.getExperienceLevel())
                .country(submissionRequest.getCountry())
                .baseSalary(submissionRequest.getBaseSalary())
                .totalCompensation(submissionRequest.getTotalCompensation())
                .currency(submissionRequest.getCurrency())
                .anonymize(submissionRequest.isAnonymize())
                .status(Status.PENDING)
                .build();

        submissionRepository.save(submission);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", submission.getId(), "status", "PENDING"));
    }
}
