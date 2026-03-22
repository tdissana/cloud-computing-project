package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.dto.ApiResponse;
import lk.watupa.bff.dto.StatsDto;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * BFF Stats Controller
 *
 * Routes:
 *   GET /bff/api/stats  →  stats-service  GET /api/stats
 *
 * PUBLIC route — no JWT required.
 * Query params (role, company, level, country) are forwarded as-is.
 */
@Slf4j
@RestController
@RequestMapping("/bff/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ProxyService      proxyService;
    private final ServiceProperties serviceProperties;

    /**
     * GET /bff/api/stats?role=&company=&level=&country=
     *
     * All params are optional. Forwards them to stats-service and returns:
     *   200 OK   { success: true, data: { count, average, median, p90, ... } }
     *   4xx/5xx  { success: false, error: "..." }
     *
     * Plain string URL is built deliberately so RestTemplate performs
     * a single encoding pass — avoiding the double-encode issue that
     * occurs when UriComponentsBuilder.toUriString() pre-encodes values
     * before RestTemplate encodes them a second time.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<StatsDto.StatsResponse>> getStats(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String country
    ) {
        log.info("Stats request — role={}, company={}, level={}, country={}", role, company, level, country);

        // Spring's @RequestParam has already decoded the incoming values.
        // Append them as plain strings so RestTemplate encodes exactly once.
        StringBuilder targetUrl = new StringBuilder(serviceProperties.getStats().getUrl() + "/api/stats");
        List<String> params = new ArrayList<>();
        if (role    != null) params.add("role="    + role);
        if (company != null) params.add("company=" + company);
        if (level   != null) params.add("level="   + level);
        if (country != null) params.add("country=" + country);
        if (!params.isEmpty()) targetUrl.append("?").append(String.join("&", params));

        ResponseEntity<StatsDto.StatsResponse> downstream = proxyService.forward(
                targetUrl.toString(),
                HttpMethod.GET,
                null,
                StatsDto.StatsResponse.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}