package lk.watupa.stats.service;

import lk.watupa.stats.client.SalarySubmissionClient;
import lk.watupa.stats.payload.SalaryStatsResponse;
import lk.watupa.stats.payload.SubmissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final SalarySubmissionClient salarySubmissionClient;

    public SalaryStatsResponse getStats(String role, String company, String level, String country) {

        List<SubmissionDto> submissions = salarySubmissionClient.getApprovedSubmissions(
                role, company, level, country
        );

        if (submissions.isEmpty()) {
            return SalaryStatsResponse.builder()
                    .role(role)
                    .company(company)
                    .level(level)
                    .country(country)
                    .count(0)
                    .average(0.0)
                    .median(0.0)
                    .p90(0.0)
                    .build();
        }

        List<Double> salaries = submissions.stream()
                .map(SubmissionDto::totalCompensation)
                .sorted()
                .toList();

        return SalaryStatsResponse.builder()
                .role(role)
                .company(company)
                .level(level)
                .country(country)
                .count(salaries.size())
                .average(computeAverage(salaries))
                .median(computePercentile(salaries, 50))
                .p90(computePercentile(salaries, 90))
                .build();
    }

    private double computeAverage(List<Double> sorted) {
        return sorted.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double computePercentile(List<Double> sorted, double percentile) {
        if (sorted.size() == 1) return sorted.get(0);

        double index = (percentile / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);

        if (lower == upper) return sorted.get(lower);

        double fraction = index - lower;
        return sorted.get(lower) + fraction * (sorted.get(upper) - sorted.get(lower));
    }
}