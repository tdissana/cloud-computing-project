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
     * GET /bff/api/search?company=...&role=...&experienceLevel=...&country=...
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
