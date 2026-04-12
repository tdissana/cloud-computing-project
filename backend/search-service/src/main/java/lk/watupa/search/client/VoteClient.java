package lk.watupa.search.client;

import lk.watupa.search.payload.VoteCountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class VoteClient {

    private final RestClient restClient;

    public VoteClient(
            RestClient.Builder builder,
            @Value("${services.vote.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<VoteCountDto> getVoteCounts(List<String> submissionIds) {
        if (submissionIds == null || submissionIds.isEmpty()) {
            return List.of();
        }
        log.debug("Calling vote-service POST /api/votes/counts with {} ids", submissionIds.size());
        List<VoteCountDto> result = restClient.post()
                .uri("/api/votes/counts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(submissionIds)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return result != null ? result : List.of();
    }
}
