package lk.watupa.identity.controller;

import jakarta.validation.Valid;
import lk.watupa.identity.payload.LoginRequest;
import lk.watupa.identity.payload.LoginResponse;
import lk.watupa.identity.payload.SignupRequest;
import lk.watupa.identity.payload.SignupResponse;
import lk.watupa.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}