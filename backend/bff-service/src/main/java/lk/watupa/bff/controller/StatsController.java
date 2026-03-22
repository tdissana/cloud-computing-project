package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.payload.ApiResponse;
import lk.watupa.bff.payload.StatsPayload;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bff/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ProxyService      proxyService;
    private final ServiceProperties serviceProperties;

    @GetMapping
    public ResponseEntity<ApiResponse<StatsPayload.StatsResponse>> getStats(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String country
    ) {
        log.info("Stats request — role={}, company={}, level={}, country={}", role, company, level, country);

        StringBuilder targetUrl = new StringBuilder(serviceProperties.getStats().getUrl() + "/api/stats");

        List<String> params = new ArrayList<>();
        if (role    != null) params.add("role="    + role);
        if (company != null) params.add("company=" + company);
        if (level   != null) params.add("level="   + level);
        if (country != null) params.add("country=" + country);
        if (!params.isEmpty()) targetUrl.append("?").append(String.join("&", params));

        ResponseEntity<StatsPayload.StatsResponse> downstream = proxyService.forward(
                targetUrl.toString(),
                HttpMethod.GET,
                null,
                StatsPayload.StatsResponse.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}