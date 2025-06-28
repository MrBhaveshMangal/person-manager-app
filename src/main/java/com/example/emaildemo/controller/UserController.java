package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User newUser) {
        // username ya password blank check karne ke liye @Valid ya @NotBlank annotation lagao User model par
        User savedUser = userService.registerUser(newUser.getUsername(), newUser.getPassword());
        return ResponseEntity.ok(savedUser);
    }
    @GetMapping("/whoami")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("User not found"));
    }
    @GetMapping("/userId")
    public ResponseEntity<String> getUserId(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user.getId());
    }


}
