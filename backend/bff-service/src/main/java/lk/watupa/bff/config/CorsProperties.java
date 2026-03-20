package lk.watupa.bff.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    /**
     * Comma-separated list of allowed origins injected via env var:
     *   CORS_ALLOWED_ORIGINS=https://techsalary.com,https://www.techsalary.com
     */
    private List<String> allowedOrigins = List.of("http://localhost:3000");
}
