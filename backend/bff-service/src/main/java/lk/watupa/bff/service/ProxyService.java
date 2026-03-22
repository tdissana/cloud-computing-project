package lk.watupa.bff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.watupa.bff.exception.DownstreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final RestTemplate restTemplate;

    public <Req, Res> ResponseEntity<Res> forward(
            String url,
            HttpMethod method,
            Req body,
            Class<Res> responseType
    ) {
        return forward(url, method, body, null, responseType);
    }

    public <Req, Res> ResponseEntity<Res> forward(
            String url,
            HttpMethod method,
            Req body,
            Map<String, String> extraHeaders,
            Class<Res> responseType
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (extraHeaders != null) {
            extraHeaders.forEach(headers::set);
        }

        HttpEntity<Req> entity = new HttpEntity<>(body, headers);

        log.debug("→ {} {}", method, url);

        try {
            ResponseEntity<Res> response = restTemplate.exchange(url, method, entity, responseType);
            log.debug("← {} {}", response.getStatusCode(), url);
            return response;

        } catch (HttpStatusCodeException ex) {
            String message = extractMessage(ex);
            log.warn("Downstream {} responded with {}: {}", url, ex.getStatusCode(), message);
            throw new DownstreamException(
                    HttpStatus.valueOf(ex.getStatusCode().value()),
                    message
            );
        }
    }

    private String extractMessage(HttpStatusCodeException ex) {
        try {
            String body = ex.getResponseBodyAsString();

            if (body.isBlank()) {
                return ex.getStatusText();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);

            if (root.has("message") && !root.get("message").asText().isBlank()) {
                return root.get("message").asText();
            }

            if (root.has("error") && !root.get("error").asText().isBlank()) {
                return root.get("error").asText();
            }

            if (root.has("status")) {
                return "Authentication failed";
            }

            return ex.getStatusText();

        } catch (Exception e) {
            return ex.getStatusText();
        }
    }
}
