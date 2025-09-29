package com.example.newsbackend.service;

import com.example.newsbackend.model.User;
import com.example.newsbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }
        User user = new User(username, email, passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String emailOrUsername, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(emailOrUsername);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(emailOrUsername);
        }

        return userOpt.filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }
}
