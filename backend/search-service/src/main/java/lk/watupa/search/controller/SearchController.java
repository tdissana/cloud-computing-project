package lk.watupa.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.watupa.search.exception.ErrorResponse;
import lk.watupa.search.payload.*;
import lk.watupa.search.service.SearchService;
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
@Tag(name = "Salary Search", description = "Search and filter salary information")
public class SearchController {

    private final SearchService searchService;

    /**
     * GET /api/search/salaries
     * 
     * Legacy GET endpoint. Filter parameters (all optional) can be passed as query parameters.
     * For complex searches with many filters, prefer using POST endpoint.
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
    @Operation(summary = "Search salaries (GET - legacy)", description = "Query salaries using URL query parameters. For complex filters, use POST endpoint instead.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful search", content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<SalaryResultResponse>> searchSalaries(
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

        log.info("Salary search request (GET): country={}, company={}, jobTitle={}, level={}, type={}, currency={}, page={}, size={}",
                country, company, jobTitle, seniorityLevel, employmentType, currency, page, size);

        return ResponseEntity.ok(searchService.search(request));
    }

    /**
     * POST /api/search/salaries
     * 
     * Preferred POST endpoint. Request body contains structured search criteria.
     * All filter fields are optional and case-insensitive for enum-like values.
     *
     * Request body structure:
     * {
     *   "country": "Sri Lanka",
     *   "company": "WSO2",
     *   "jobTitle": "Software Engineer",
     *   "seniorityLevel": "Mid",           // Case-insensitive: Junior, Mid, Senior, Lead, Manager
     *   "employmentType": "Full-time",     // Case-insensitive: Full-time, Part-time, Contract, Freelance
     *   "currency": "LKR",                 // Case-insensitive: USD, LKR, EUR, etc.
     *   "minExperience": 2,
     *   "maxExperience": 10,
     *   "page": 0,                         // 0-based pagination
     *   "size": 20,                        // Max 100
     *   "sortBy": "approvedAt",            // Case-insensitive: grossMonthlySalary, yearsOfExperience, approvedAt, upvotes
     *   "sortDir": "desc"                  // Case-insensitive: asc, desc
     * }
     */
    @PostMapping("/salaries")
    @Operation(summary = "Search salaries (POST - recommended)", description = "Query salaries using a structured JSON request body. Supports all filters. All enum fields are case-insensitive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful search", content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            // @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            // @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<SalaryResultResponse>> searchSalariesPost(
            @Valid @RequestBody SalarySearchRequest request
    ) {
        log.info("Salary search request (POST): country={}, company={}, jobTitle={}, level={}, type={}, currency={}, minExp={}, maxExp={}, page={}, size={}, sortBy={}, sortDir={}",
                request.getCountry(), request.getCompany(), request.getJobTitle(),
                request.getSeniorityLevel(), request.getEmploymentType(), request.getCurrency(),
                request.getMinExperience(), request.getMaxExperience(),
                request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());

        return ResponseEntity.ok(searchService.search(request));
    }

    /**
     * GET /api/search/filters
     * Returns distinct values for all filterable fields (for dropdown population).
     */
    @GetMapping("/filters")
    @Operation(summary = "Get available filter options", description = "Returns all available values for filterable fields like seniority levels, employment types, currencies, etc.")
    @ApiResponse(responseCode = "200", description = "Filter options retrieved", content = @Content(schema = @Schema(implementation = FilterOptionsResponse.class)))
    public ResponseEntity<FilterOptionsResponse> getFilterOptions() {
        return ResponseEntity.ok(searchService.getFilterOptions());
    }
}