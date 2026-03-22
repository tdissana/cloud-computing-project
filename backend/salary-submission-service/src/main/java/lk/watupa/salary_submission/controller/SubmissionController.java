package lk.watupa.salary_submission.controller;

import jakarta.validation.Valid;
import lk.watupa.salary_submission.payload.SubmissionRequest;
import lk.watupa.salary_submission.service.SubmissionService;
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

    private final SubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody SubmissionRequest req) {

        Long submissionId = submissionService.submit(
                req.getCompanyName(),
                req.getJobTitle(),
                req.getExperienceLevel(),
                req.getSeniority(),
                req.getCountry(),
                req.getCurrency(),
                req.getTotalCompensation(),
                req.getBaseSalary(),
                req.getSkills(),
                req.isAnonymize()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", submissionId, "status", "PENDING"));
    }
}
