package lk.watupa.identity.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 10)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 12)
    private String password;
}
