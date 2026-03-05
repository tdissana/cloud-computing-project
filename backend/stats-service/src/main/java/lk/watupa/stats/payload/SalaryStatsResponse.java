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
    private String role;
    private String company;
    private String level;
    private String country;
    private long    count;
    private double  average;
    private double  median;
    private double  p90;
}