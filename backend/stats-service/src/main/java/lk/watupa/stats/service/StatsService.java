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

    public SalaryStatsResponse getStats(String jobTitle, String company, String seniorityLevel, String country, String employmentType, String currency) {

        List<SubmissionDto> submissions = salarySubmissionClient.getApprovedSubmissions(
                jobTitle, company, seniorityLevel, country, employmentType, currency
        );

        if (submissions.isEmpty()) {
            return SalaryStatsResponse.builder()
                    .jobTitle(jobTitle)
                    .company(company)
                    .seniorityLevel(seniorityLevel)
                    .country(country)
                    .employmentType(employmentType)
                    .currency(currency)
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
                .jobTitle(jobTitle)
                .company(company)
                .seniorityLevel(seniorityLevel)
                .country(country)
                .employmentType(employmentType)
                .currency(currency)
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