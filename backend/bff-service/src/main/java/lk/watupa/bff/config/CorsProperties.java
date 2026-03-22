package lk.watupa.bff.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:3000");
}
