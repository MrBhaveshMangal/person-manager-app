package com.example.emaildemo.service;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String rawPassword, String email) {

        String u  = username.trim().toLowerCase();
        String em = email.trim().toLowerCase();

        if (userRepository.findByUsernameIgnoreCase(u).isPresent())
            throw new RuntimeException("Username already exists");

        if (userRepository.findByEmailIgnoreCase(em).isPresent())
            throw new RuntimeException("Email already registered");

        User user = new User();
        user.setUsername(u);
        user.setEmail(em);
        user.setPassword(passwordEncoder.encode(rawPassword));   // 🔑 encode

        userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username.trim().toLowerCase());
    }

    public Optional<User> getUserByEmail(String email) { // ✅ ✅ ✅ ADDED THIS METHOD
        return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase());
    }

    public User findByUsername(String username) {
        return getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public User findByEmail(String email) {
        return getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
