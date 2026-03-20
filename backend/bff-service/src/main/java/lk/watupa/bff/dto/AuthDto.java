package lk.watupa.bff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ─── Auth DTOs (mirror identity-service payload classes) ─────────────────────

public class AuthDto {

    // ── Signup ────────────────────────────────────────────────────────────────

    @Data
    public static class SignupRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 10, message = "Username must be 3–10 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Size(max = 50)
        @Email(message = "Enter a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 5, max = 12, message = "Password must be 5–12 characters")
        private String password;
    }

    @Data
    public static class SignupResponse {
        private String message;
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 10, message = "Username must be 3–10 characters")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 5, max = 12, message = "Password must be 5–12 characters")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String message;
        private String token;
    }
}
