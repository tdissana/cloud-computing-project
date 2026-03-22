package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class HealthController {

    private final ServiceProperties serviceProperties;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> data = Map.of(
                "service", "bff-service",
                "version", "1.0.0",
                "timestamp", Instant.now().toString(),
                "upstreamServices", Map.of(
                        "identity",         serviceProperties.getIdentity().getUrl(),
                        "salarySubmission",  serviceProperties.getSalarySubmission().getUrl(),
                        "search",           serviceProperties.getSearch().getUrl(),
                        "stats",            serviceProperties.getStats().getUrl(),
                        "vote",             serviceProperties.getVote().getUrl()
                )
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
