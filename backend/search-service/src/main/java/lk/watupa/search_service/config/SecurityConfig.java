package lk.watupa.search_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the search service.
 *
 * The search service is read-only and publicly accessible — no authentication
 * is required to search salaries. The BFF layer enforces auth for actions that
 * require it (voting, reporting), so this service only needs to:
 *   1. Permit all GET requests to /api/search/**
 *   2. Permit actuator health/readiness endpoints (for Kubernetes probes)
 *   3. Disable sessions (stateless REST service)
 *   4. Disable CSRF (not needed for a stateless API)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/search/**",
                                "/actuator/health",
                                "/actuator/health/readiness",
                                "/actuator/health/liveness",
                                "/actuator/info"
                        ).permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}