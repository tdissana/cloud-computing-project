package lk.watupa.bff.controller;

import jakarta.validation.Valid;
import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.dto.ApiResponse;
import lk.watupa.bff.dto.AuthDto;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BFF Auth Controller
 *
 * Routes:
 *   POST /bff/api/auth/signup  →  identity-service  POST /api/auth/signup
 *   POST /bff/api/auth/login   →  identity-service  POST /api/auth/login
 *
 * These are PUBLIC routes — no JWT required.
 * The BFF re-validates the request body before forwarding so bad payloads
 * never reach the identity-service.
 */
@Slf4j
@RestController
@RequestMapping("/bff/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ProxyService proxyService;
    private final ServiceProperties serviceProperties;

    // ── Signup ────────────────────────────────────────────────────────────────

    /**
     * POST /bff/api/auth/signup
     *
     * Forwards the signup request to identity-service and returns:
     *   201 Created  { success: true, data: { message: "..." } }
     *   4xx/5xx      { success: false, error: "..." }
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthDto.SignupResponse>> signup(
            @Valid @RequestBody AuthDto.SignupRequest request
    ) {
        log.info("Signup request for username={}", request.getUsername());

        String targetUrl = serviceProperties.getIdentity().getUrl() + "/api/auth/signup";

        ResponseEntity<AuthDto.SignupResponse> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                request,
                AuthDto.SignupResponse.class
        );

        // Relay the exact HTTP status the identity-service sent (201)
        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    /**
     * POST /bff/api/auth/login
     *
     * Forwards the login request and returns the JWT token to the client.
     *   200 OK   { success: true, data: { message: "...", token: "eyJ..." } }
     *   401      { success: false, error: "Invalid credentials" }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        log.info("Login request for username={}", request.getUsername());

        String targetUrl = serviceProperties.getIdentity().getUrl() + "/api/auth/login";

        ResponseEntity<AuthDto.LoginResponse> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                request,
                AuthDto.LoginResponse.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}
