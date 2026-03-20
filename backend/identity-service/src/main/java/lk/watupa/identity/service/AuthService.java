package lk.watupa.identity.service;

import lk.watupa.identity.model.User;
import lk.watupa.identity.payload.LoginResponse;
import lk.watupa.identity.payload.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public SignupResponse signup(String username, String email, String password) {

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

    public LoginResponse login(String username, String password) {

        User user = userService.getUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUserId().toString());

        return new LoginResponse("Login successful", token);
    }
}