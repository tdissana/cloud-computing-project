package lk.watupa.search.controller;

import jakarta.validation.Valid;
import lk.watupa.search.payload.*;
import lk.watupa.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * POST /api/search/salaries
     * Searches salary.submission; scope is controlled by verificationStatus (VERIFIED = APPROVED, UNVERIFIED = not approved).
     */
    @PostMapping("/salaries")
    public ResponseEntity<PagedResponse<SalaryResultResponse>> searchSalaries(
            @Valid @RequestBody SalarySearchRequest request
    ) {
        log.info("Salary search (POST): country={}, company={}, jobTitle={}, level={}, employmentType={}, currency={}, " +
                        "minSeniority={}, maxSeniority={}, verificationStatus={}, page={}, size={}, sortBy={}, sortDir={}",
                request.getCountry(), request.getCompany(), request.getJobTitle(),
                request.getExperienceLevel(), request.getEmploymentType(), request.getCurrency(),
                request.getMinSeniority(), request.getMaxSeniority(), request.getVerificationStatus(),
                request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());

        return ResponseEntity.ok(searchService.search(request));
    }

    /**
     * GET /api/search/filters
     * Returns distinct values for filterable fields.
     */
    @GetMapping("/filters")
    public ResponseEntity<FilterOptionsResponse> getFilterOptions() {
        return ResponseEntity.ok(searchService.getFilterOptions());
    }
}
