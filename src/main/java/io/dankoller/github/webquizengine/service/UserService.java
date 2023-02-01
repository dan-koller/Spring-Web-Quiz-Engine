package io.dankoller.github.webquizengine.service;

import io.dankoller.github.webquizengine.entity.user.User;
import io.dankoller.github.webquizengine.persistence.UserRepository;
import io.dankoller.github.webquizengine.request.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method handles the registration of a new user.
     *
     * @param request The registration request
     * @return A ResponseEntity with the result of the registration
     */
    public ResponseEntity<?> register(RegistrationRequest request) {
        // Check if user already exists
        if (userRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate registration request
        if (isValidRegistrationRequest(request)) {
            // Create a new user
            User user = new User(request.getEmail(), request.getPassword());
            // Salt the password and encode it
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Save the user
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method checks if the registration request is valid.
     *
     * @param request The registration request
     * @return True if the request is valid, false otherwise
     */
    private boolean isValidRegistrationRequest(RegistrationRequest request) {
        return request.getEmail() != null && request.getPassword() != null;
    }
}
