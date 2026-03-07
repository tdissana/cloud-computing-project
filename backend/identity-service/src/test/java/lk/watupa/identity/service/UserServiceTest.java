package lk.watupa.identity.service;

import lk.watupa.identity.model.User;
import lk.watupa.identity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ShouldSaveUser() {

        String username = "testuser";
        String email = "test@test.com";
        String password = "password123";

        userService.registerUser(username, email, password);

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void isUsernameExist_ShouldReturnTrue() {

        when(userRepository.existsByUserName("testuser"))
                .thenReturn(true);

        boolean result = userService.isUsernameExist("testuser");

        assert(result);
    }

    @Test
    void isEmailExist_ShouldReturnTrue() {

        when(userRepository.existsByemail("test@test.com"))
                .thenReturn(true);

        boolean result = userService.isEmailExist("test@test.com");

        assert(result);
    }
}