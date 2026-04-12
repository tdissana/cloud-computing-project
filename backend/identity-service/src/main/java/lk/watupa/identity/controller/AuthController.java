package lk.watupa.identity.controller;

import jakarta.validation.Valid;
import lk.watupa.identity.payload.LoginRequest;
import lk.watupa.identity.payload.LoginResponse;
import lk.watupa.identity.payload.SignupRequest;
import lk.watupa.identity.payload.SignupResponse;
import lk.watupa.identity.service.AuthService;
import lk.watupa.identity.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword());
        return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid Authorization header"));
        }
        String token = authorization.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
        String userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(Map.of("userId", userId));
    }
}