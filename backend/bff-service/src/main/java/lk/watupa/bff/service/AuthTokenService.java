package lk.watupa.bff.service;

import lk.watupa.bff.config.ServiceProperties;
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
public class AuthTokenService {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    public String validateAndExtractUserId(String bearerToken) {
        String validateUrl = serviceProperties.getIdentity().getUrl() + "/api/auth/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        log.debug("Validating token at {}", validateUrl);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    validateUrl, HttpMethod.GET, entity, Map.class
            );

            Object userId = response.getBody() != null ? response.getBody().get("userId") : null;
            if (userId == null) {
                throw new DownstreamException(HttpStatus.UNAUTHORIZED, "Token validation returned no userId");
            }

            return userId.toString();

        } catch (HttpStatusCodeException ex) {
            log.warn("Token validation failed: {}", ex.getStatusCode());
            throw new DownstreamException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
