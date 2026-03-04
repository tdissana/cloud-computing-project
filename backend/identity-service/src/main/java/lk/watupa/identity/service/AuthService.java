package lk.watupa.identity.service;

import lk.watupa.identity.payload.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signUp(String username, String email, String password) {

        boolean usernameExists = userService.isUsernameExist(username);
        if (usernameExists) {
            throw new RuntimeException("Username is already taken!");
        }

        boolean emailExists = userService.isEmailExist(email);
        if (emailExists) {
            throw new RuntimeException("Email is already taken!");
        }

        userService.registerUser(username, email, passwordEncoder.encode(password));

        return new SignupResponse("User registered successfully!");
    }
}