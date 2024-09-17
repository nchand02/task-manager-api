package com.encora.taskmanager.service;

import com.encora.taskmanager.dto.UserDto;
import com.encora.taskmanager.entity.User;
import com.encora.taskmanager.exception.ResourceAlreadyExistsException;
import com.encora.taskmanager.repository.UserRepository;
import com.encora.taskmanager.security.JWTService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);;
    }

    public User createUser(UserDto userDto) {
        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setEmail(userDto.getEmail());
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Check for email unique constraint violation
            if (ex.getCause() instanceof ConstraintViolationException &&
                    ex.getCause().getMessage().contains("UK_USER_EMAIL")) {
                throw new ResourceAlreadyExistsException("Email address is already in use.");
            }
            // Check for username unique constraint violation (add this check)
            else if (ex.getCause() instanceof ConstraintViolationException &&
                    ex.getCause().getMessage().contains("UK_USER_USERNAME")) { // Assuming your username constraint is named "UK_USER_USERNAME"
                throw new ResourceAlreadyExistsException("Username is already taken.");
            }
            else {
                // Handle other database exceptions
                throw new RuntimeException("Error saving user.", ex);
            }
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public String verify(User user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername())  ;
        } else {
            return "fail";
        }
    }
}