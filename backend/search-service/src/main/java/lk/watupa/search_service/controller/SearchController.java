package lk.watupa.search_service.controller;

import lk.watupa.search_service.dto.*;
import lk.watupa.search_service.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for salary search.
 * All endpoints are publicly accessible (no authentication required).
 * The BFF sits in front of this service in production.
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * GET /api/search/salaries
     *
     * Filter parameters (all optional):
     *   country, company, jobTitle, seniorityLevel, employmentType,
     *   currency, minExperience, maxExperience,
     *   page (0-based), size (default 20, max 100),
     *   sortBy (grossMonthlySalary | yearsOfExperience | approvedAt | upvotes),
     *   sortDir (asc | desc)
     *
     * Returns a paginated list of approved, anonymized-safe salary entries.
     */
    @GetMapping("/salaries")
    public ResponseEntity<PagedResponse<SalaryResultDto>> searchSalaries(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String seniorityLevel,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "approvedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        SalarySearchRequest request = new SalarySearchRequest();
        request.setCountry(country);
        request.setCompany(company);
        request.setJobTitle(jobTitle);
        request.setSeniorityLevel(seniorityLevel);
        request.setEmploymentType(employmentType);
        request.setCurrency(currency);
        request.setMinExperience(minExperience);
        request.setMaxExperience(maxExperience);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);

        log.info("Salary search request: country={}, company={}, jobTitle={}, level={}, page={}",
                country, company, jobTitle, seniorityLevel, page);

        return ResponseEntity.ok(searchService.search(request));
    }

    /**
     * GET /api/search/filters
     * Returns distinct values for all filterable fields (for dropdown population).
     */
    @GetMapping("/filters")
    public ResponseEntity<FilterOptionsDto> getFilterOptions() {
        return ResponseEntity.ok(searchService.getFilterOptions());
    }
}