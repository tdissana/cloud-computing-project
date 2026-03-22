package lk.watupa.bff.payload;

import lombok.Data;

public class StatsPayload {

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