package lk.watupa.salary_submission_service.controller;

import lk.watupa.salary_submission_service.enums.Status;
import lk.watupa.salary_submission_service.payload.Submission;
import lk.watupa.salary_submission_service.payload.SubmissionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class AuthController {

    @PostMapping  // NO auth required
    public ResponseEntity<?> submit(@RequestBody SubmissionRequest submissionRequest) {
        Submission submission= new Submission();
        submission.setCompany(submissionRequest.getCompany());
        submission.setRole(submissionRequest.getRole());
        submission.setExperienceLevel(submissionRequest.getExperienceLevel());
        submission.setBaseSalary(submissionRequest.getBaseSalary());
        submission.setTotalCompensation(submissionRequest.getTotalCompensation());
        submission.setAnonymize(submissionRequest.isAnonymize());
        submission.setStatus(Status.PENDING.PENDING);
        // NO email stored
        submissionRepository.save(submission);
        return ResponseEntity.ok(Map.of("id", submission.getSubmission_id(), "status", "PENDING"));
    }
}
