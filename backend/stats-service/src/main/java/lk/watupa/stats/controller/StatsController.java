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
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String seniorityLevel,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String currency
    ) {
        SalaryStatsResponse response = statsService.getStats(jobTitle, company, seniorityLevel, country, employmentType, currency);
        return ResponseEntity.ok(response);
    }
}