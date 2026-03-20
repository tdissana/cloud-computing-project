package lk.watupa.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${http-client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${http-client.read-timeout:10000}")
    private int readTimeout;

    /**
     * Single shared RestTemplate with sensible timeouts.
     * In production you would replace this with WebClient (reactive)
     * or a dedicated Feign client per service.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
