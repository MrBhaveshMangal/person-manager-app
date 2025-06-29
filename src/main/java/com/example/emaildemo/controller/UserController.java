package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User newUser) {
        User savedUser = userService.registerUser(newUser.getUsername(), newUser.getPassword());
        return ResponseEntity.ok(savedUser);
    }

    // Dummy login endpoint (Spring Security handles real auth)
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
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
