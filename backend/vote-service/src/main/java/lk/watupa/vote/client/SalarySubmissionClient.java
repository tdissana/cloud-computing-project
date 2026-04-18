package lk.watupa.vote.client;

import lk.watupa.vote.payload.SubmissionStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public SubmissionStatusDto updateStatus(String submissionId, String newStatus) {
        log.debug("Calling salary-submission-service PATCH /api/submissions/{}/status", submissionId);
        return restClient.patch()
                .uri("/api/submissions/{id}/status", submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", newStatus))
                .retrieve()
                .body(SubmissionStatusDto.class);
    }
}
