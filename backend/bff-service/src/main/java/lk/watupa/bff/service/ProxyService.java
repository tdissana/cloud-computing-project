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

/**
 * Generic HTTP proxy helper.
 *
 * All BFF controllers delegate actual HTTP calls to this service so that:
 *  - Error extraction logic lives in one place.
 *  - Headers (Authorization, X-User-Id) are forwarded consistently.
 *  - Downstream error messages bubble up cleanly to the client.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final RestTemplate restTemplate;

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Forward a request to a downstream service without any auth header.
     * Used for public endpoints (signup, login, search, submit salary, stats).
     */
    public <Req, Res> ResponseEntity<Res> forward(
            String url,
            HttpMethod method,
            Req body,
            Class<Res> responseType
    ) {
        return forward(url, method, body, null, responseType);
    }

    /**
     * Forward a request, optionally injecting extra headers.
     * Used for protected endpoints (vote, report) where X-User-Id must be set.
     */
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
            // Extract the message from the downstream JSON error body if possible
            String message = extractMessage(ex);
            log.warn("Downstream {} responded with {}: {}", url, ex.getStatusCode(), message);
            throw new DownstreamException(
                    HttpStatus.valueOf(ex.getStatusCode().value()),
                    message
            );
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Tries to parse { "message": "..." } from the downstream error body.
     * Falls back to the raw body string if parsing fails.
     */
    private String extractMessage(HttpStatusCodeException ex) {
        try {
            String body = ex.getResponseBodyAsString();

            if (body == null || body.isBlank()) {
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
