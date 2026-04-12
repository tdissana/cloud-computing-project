package lk.watupa.stats.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryStatsResponse {
    private String jobTitle;
    private String company;
    private String seniorityLevel;
    private String country;
    private String employmentType;
    private String currency;
    private long    count;
    private double  average;
    private double  median;
    private double  p90;
}