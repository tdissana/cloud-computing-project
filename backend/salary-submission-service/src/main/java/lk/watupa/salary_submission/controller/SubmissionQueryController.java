package lk.watupa.salary_submission.controller;

import jakarta.validation.Valid;
import lk.watupa.salary_submission.payload.*;
import lk.watupa.salary_submission.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionQueryController {

    private final SubmissionQueryService queryService;

    @PostMapping("/search")
    public ResponseEntity<PagedDto<SubmissionDto>> search(
            @RequestBody(required = false) SubmissionSearchRequest request) {
        if (request == null) {
            request = new SubmissionSearchRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return ResponseEntity.ok(queryService.search(request));
    }

    @GetMapping("/filters")
    public ResponseEntity<FilterOptionsDto> getFilterOptions() {
        return ResponseEntity.ok(queryService.getFilterOptions());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SubmissionDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(queryService.updateStatus(id, request.status()));
    }
}
