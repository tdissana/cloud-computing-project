package lk.watupa.stats.controller;

import lk.watupa.stats.payload.SalaryStatsResponse;
import lk.watupa.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<SalaryStatsResponse> getStats(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String country
    ) {
        SalaryStatsResponse response = statsService.getStats(role, company, level, country);
        return ResponseEntity.ok(response);
    }
}