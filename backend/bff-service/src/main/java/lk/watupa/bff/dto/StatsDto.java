package lk.watupa.bff.dto;

import lombok.Data;

/**
 * DTOs for the stats-service contract.
 */
public class StatsDto {

    @Data
    public static class StatsResponse {
        private String role;
        private String company;
        private String level;
        private String country;
        private long   count;
        private double average;
        private double median;
        private double p90;
    }
}