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
 * POST  /bff/api/submissions          → salary-submission-service
 * GET   /bff/api/search               → search-service
 * GET   /bff/api/stats                → stats-service
 */
@Slf4j
@RestController
@RequestMapping("/bff/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProxyService proxyService;
    private final ServiceProperties serviceProperties;

    // ── Submit salary (no auth needed) ───────────────────────────────────────
    /**
     * POST /bff/api/search/salaries
     *
     * Preferred frontend endpoint that forwards a structured JSON search body
     * to search-service /api/search/salaries.
     */
    @PostMapping("/salaries")
    public ResponseEntity<ApiResponse<Object>> searchSalaries(
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
    @GetMapping("/filters")
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
}
