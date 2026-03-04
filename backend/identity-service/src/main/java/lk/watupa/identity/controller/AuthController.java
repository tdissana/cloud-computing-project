package lk.watupa.identity.controller;

import lk.watupa.identity.payload.SignupRequest;
import lk.watupa.identity.payload.SignupResponse;
import lk.watupa.identity.service.AuthService;
import lk.watupa.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signUp(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword());
        return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
    }
}