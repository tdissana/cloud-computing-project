package lk.watupa.bff.controller;

import jakarta.validation.Valid;
import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.payload.ApiResponse;
import lk.watupa.bff.payload.AuthPayload;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bff/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ProxyService proxyService;
    private final ServiceProperties serviceProperties;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthPayload.SignupResponse>> signup(
            @Valid @RequestBody AuthPayload.SignupRequest request
    ) {
        log.info("Signup request for username={}", request.getUsername());

        String targetUrl = serviceProperties.getIdentity().getUrl() + "/api/auth/signup";

        ResponseEntity<AuthPayload.SignupResponse> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                request,
                AuthPayload.SignupResponse.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthPayload.LoginResponse>> login(
            @Valid @RequestBody AuthPayload.LoginRequest request
    ) {
        log.info("Login request for username={}", request.getUsername());

        String targetUrl = serviceProperties.getIdentity().getUrl() + "/api/auth/login";

        ResponseEntity<AuthPayload.LoginResponse> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                request,
                AuthPayload.LoginResponse.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}
