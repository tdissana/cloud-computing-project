package lk.watupa.bff.payload;

import lombok.Data;

public class StatsPayload {

    @Data
    public static class StatsResponse {
        private String jobTitle;
        private String company;
        private String seniorityLevel;
        private String country;
        private String employmentType;
        private String currency;
        private long   count;
        private double average;
        private double median;
        private double p90;
    }
}