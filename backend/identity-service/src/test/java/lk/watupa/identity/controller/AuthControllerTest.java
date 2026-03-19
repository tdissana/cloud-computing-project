package lk.watupa.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.watupa.identity.payload.SignupRequest;
import lk.watupa.identity.payload.SignupResponse;
import lk.watupa.identity.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signup_ShouldReturnCreatedUser() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        SignupResponse response = new SignupResponse("User registered successfully");

        when(authService.signup(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"));
    }
}