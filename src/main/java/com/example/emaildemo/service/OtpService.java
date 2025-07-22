package com.example.emaildemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void sendOtp(String email) {
        try {
            String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
            otpStorage.put(email, otp);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bhaveshmangal.1210@gmail.com");
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp + "\n\nThis OTP will expire in 5 minutes.");

            mailSender.send(message);
            System.out.println(" OTP sent to: " + email + " | OTP: " + otp);

        } catch (Exception e) {
            System.err.println(" Failed to send OTP to " + email + ": " + e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return otp != null && otp.equals(storedOtp);
    }

    // Clear OTP after use
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
}
