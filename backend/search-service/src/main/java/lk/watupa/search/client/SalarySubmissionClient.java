package lk.watupa.search.client;

import lk.watupa.search.payload.OwnerFilterOptionsDto;
import lk.watupa.search.payload.OwnerPagedDto;
import lk.watupa.search.payload.SalarySearchRequest;
import lk.watupa.search.payload.SubmissionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class SalarySubmissionClient {

    private final RestClient restClient;

    public SalarySubmissionClient(
            RestClient.Builder builder,
            @Value("${services.salary-submission.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public OwnerPagedDto<SubmissionDto> search(SalarySearchRequest request) {
        log.debug("Calling salary-submission-service POST /api/submissions/search");

        Map<String, Object> body = buildSearchBody(request);

        return restClient.post()
                .uri("/api/submissions/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public OwnerFilterOptionsDto getFilterOptions() {
        log.debug("Calling salary-submission-service GET /api/submissions/filters");
        return restClient.get()
                .uri("/api/submissions/filters")
                .retrieve()
                .body(OwnerFilterOptionsDto.class);
    }

    private Map<String, Object> buildSearchBody(SalarySearchRequest req) {
        var map = new java.util.HashMap<String, Object>();
        if (req.getCountry() != null) map.put("country", req.getCountry());
        if (req.getCompany() != null) map.put("company", req.getCompany());
        if (req.getJobTitle() != null) map.put("jobTitle", req.getJobTitle());
        if (req.getExperienceLevel() != null) map.put("experienceLevel", req.getExperienceLevel());
        if (req.getCurrency() != null) map.put("currency", req.getCurrency());
        if (req.getMinSeniority() != null) map.put("minSeniority", req.getMinSeniority());
        if (req.getMaxSeniority() != null) map.put("maxSeniority", req.getMaxSeniority());
        if (req.getVerificationStatus() != null) map.put("verificationStatus", req.getVerificationStatus());
        if (req.getPage() != null) map.put("page", req.getPage());
        if (req.getSize() != null) map.put("size", req.getSize());
        if (req.getSortBy() != null) map.put("sortBy", req.getSortBy());
        if (req.getSortDir() != null) map.put("sortDir", req.getSortDir());
        return map;
    }
}
