package lk.watupa.bff.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Centralised CORS configuration.
 *
 * Why a CorsFilter bean instead of just WebMvcConfigurer?
 *  - CorsFilter sits at the servlet-filter level, which means it also covers
 *    error-response paths and non-controller routes.
 *  - It guarantees CORS headers are present even when a request is rejected
 *    (e.g. 401 from the auth filter), so the browser can read the error body.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ── Allowed origins from config / env var ──────────────────────────
        List<String> origins = corsProperties.getAllowedOrigins();
        log.info("CORS allowed origins: {}", origins);
        config.setAllowedOrigins(origins);

        // ── Methods ────────────────────────────────────────────────────────
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // ── Headers ────────────────────────────────────────────────────────
        // Authorization is needed for protected routes (vote, report)
        config.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // ── Expose headers the browser JS can read ─────────────────────────
        config.setExposedHeaders(List.of("Authorization", "X-User-Id"));

        // ── Credentials (needed if you ever use cookies instead of Bearer) ─
        config.setAllowCredentials(true);

        // ── Preflight cache (seconds) ──────────────────────────────────────
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);   // apply to every path

        return new CorsFilter(source);
    }
}
