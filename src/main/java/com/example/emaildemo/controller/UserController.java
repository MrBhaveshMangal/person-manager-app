package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
