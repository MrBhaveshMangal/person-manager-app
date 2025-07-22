package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.*;
import com.example.emaildemo.repository.UserRepository;
import com.example.emaildemo.service.UserService;
import com.example.emaildemo.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private OtpService otpService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;

    // OTP

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest req) {
        try {
            otpService.sendOtp(req.getEmail().trim().toLowerCase());
            return ResponseEntity.ok("OTP sent to your email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (otpService.verifyOtp(email, req.getOtp())) {
            otpService.clearOtp(email);
            return ResponseEntity.ok("OTP verified.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }

    // OTP for Reset Password
    @PostMapping("/send-reset-otp")
    public ResponseEntity<?> sendResetOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email").trim().toLowerCase();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Email not found");
        }

        try {
            otpService.sendOtp(email);
            return ResponseEntity.ok("OTP sent for password reset");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email").trim().toLowerCase();
        String otp = body.get("otp");

        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        otpService.clearOtp(email);
        return ResponseEntity.ok("OTP verified");
    }

    // 👤 Registration/Login

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest req) {
        try {
            userService.registerUser(req.getUsername(), req.getPassword(), req.getEmail());

            // ✅ Send OTP after successful registration
            otpService.sendOtp(req.getEmail().trim().toLowerCase());

            return ResponseEntity.ok("User registered successfully. OTP sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/login-body")
    public ResponseEntity<?> loginWithBody(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials ❌");
        }
    }

    // Lookup & Verification
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.getUserByEmail(email).isPresent();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<?> emailExists(@RequestParam String email) {
        boolean exists = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase()).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.getUserByUsername(username).isPresent();
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody Map<String, String> payload) {
        String input = payload.get("identifier");
        if (input == null || input.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Identifier is missing.");
        }

        input = input.trim().toLowerCase();

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(input);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsernameIgnoreCase(input);
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "username", user.getUsername()
        ));
    }

    //Reset Password

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body("Invalid input");
        }

        User user;
        try {
            user = userService.findByEmail(email);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Email not found");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully");
    }

    //👥 User Info

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/userId")
    public ResponseEntity<String> getUserId(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(String.valueOf(user.getId()));
    }

    @GetMapping("/whoami")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        Optional<User> userOpt = userService.getUserByUsername(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }
}
