package lk.watupa.identity.service;

import lk.watupa.identity.model.User;
import lk.watupa.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean isUsernameExist(String username) {
        return userRepository.existsByUserName(username);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByemail(email);
    }

    public void registerUser(String username, String email, String password) {
        User user = new User(username, email, password);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error in saving user!");
        }
    }
}
