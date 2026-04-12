package lk.watupa.stats.client;

import lk.watupa.stats.payload.PagedDto;
import lk.watupa.stats.payload.SubmissionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SalarySubmissionClient {

    private static final int PAGE_SIZE = 100;

    private final RestClient restClient;

    public SalarySubmissionClient(
            RestClient.Builder builder,
            @Value("${services.salary-submission.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<SubmissionDto> getApprovedSubmissions(
            String jobTitle, String company, String level, String country, String employmentType, String currency) {

        List<SubmissionDto> all = new ArrayList<>();
        int page = 0;

        while (true) {
            Map<String, Object> body = new HashMap<>();
            body.put("verificationStatus", "VERIFIED");
            body.put("page", page);
            body.put("size", PAGE_SIZE);
            if (jobTitle != null) body.put("jobTitle", jobTitle);
            if (company != null) body.put("company", company);
            if (level != null) body.put("experienceLevel", level);
            if (country != null) body.put("country", country);
            if (employmentType != null) body.put("employmentType", employmentType);
            if (currency != null) body.put("currency", currency);

            log.debug("Fetching approved submissions page={}", page);

            PagedDto<SubmissionDto> result = restClient.post()
                    .uri("/api/submissions/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (result == null || result.content() == null || result.content().isEmpty()) {
                break;
            }

            all.addAll(result.content());

            if (result.last()) {
                break;
            }
            page++;
        }

        return all;
    }
}
