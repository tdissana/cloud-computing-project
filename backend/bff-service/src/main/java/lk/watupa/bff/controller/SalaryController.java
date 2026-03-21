package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.dto.ApiResponse;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * BFF Salary Controller  — all routes are PUBLIC (no login required)
 *
 * POST  /bff/api/submissions            → salary-submission-service
 * POST  /bff/api/search/salaries        → search-service
 * GET   /bff/api/search/filters         → search-service
 * GET   /bff/api/search                 → search-service (legacy passthrough)
 * GET   /bff/api/stats                  → stats-service
 */
@Slf4j
@RestController
@RequestMapping("/bff/api")
@RequiredArgsConstructor
public class SalaryController {

    private final ProxyService proxyService;
    private final ServiceProperties serviceProperties;

    // ── Submit salary (no auth needed) ───────────────────────────────────────

    /**
     * POST /bff/api/submissions
     * Body: { company, role, experienceLevel, baseSalary, totalCompensation,
     *         country, currency, anonymize }
     */
    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<Map>> submitSalary(
            @RequestBody Map<String, Object> body
    ) {
        log.info("Salary submission request received");

        String targetUrl = serviceProperties.getSalarySubmission().getUrl() + "/api/submissions";

        ResponseEntity<Map> downstream = proxyService.forward(
                targetUrl, HttpMethod.POST, body, Map.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    // ── Search salaries (no auth needed) ─────────────────────────────────────

    /**
     * POST /bff/api/search/salaries
     *
     * Preferred frontend endpoint that forwards a structured JSON search body
     * to search-service /api/search/salaries.
     */
    @PostMapping("/search/salaries")
    public ResponseEntity<ApiResponse<Object>> searchSalariesPost(
            @RequestBody(required = false) Map<String, Object> body
    ) {
        Map<String, Object> safeBody = (body == null) ? Map.of() : body;
        log.debug("Search POST request body keys={}", safeBody.keySet());

        String targetUrl = serviceProperties.getSearch().getUrl() + "/api/search/salaries";

        ResponseEntity<Object> downstream = proxyService.forward(
                targetUrl, HttpMethod.POST, safeBody, Object.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    /**
     * GET /bff/api/search/filters
     *
     * Returns filter options used by the search page dropdowns.
     */
    @GetMapping("/search/filters")
    public ResponseEntity<ApiResponse<Object>> getSearchFilterOptions() {
        log.debug("Search filter options request received");

        String targetUrl = serviceProperties.getSearch().getUrl() + "/api/search/filters";

        ResponseEntity<Object> downstream = proxyService.forward(
                targetUrl, HttpMethod.GET, null, Object.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    /**
     * GET /bff/api/search?company=...&role=...&experienceLevel=...&country=...
     *
     * Legacy passthrough endpoint kept for backward compatibility.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchSalaries(
            @RequestParam Map<String, String> params
    ) {
        log.debug("Search request params={}", params);

        String qs = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .map(s -> "?" + s)
                .orElse("");

        String targetUrl = serviceProperties.getSearch().getUrl() + "/api/search" + qs;

        ResponseEntity<Object> downstream = proxyService.forward(
                targetUrl, HttpMethod.GET, null, Object.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    // ── Stats (no auth needed) ────────────────────────────────────────────────

    /**
     * GET /bff/api/stats?role=...&country=...
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats(
            @RequestParam Map<String, String> params
    ) {
        log.debug("Stats request params={}", params);

        String qs = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .map(s -> "?" + s)
                .orElse("");

        String targetUrl = serviceProperties.getStats().getUrl() + "/api/stats" + qs;

        ResponseEntity<Object> downstream = proxyService.forward(
                targetUrl, HttpMethod.GET, null, Object.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}
