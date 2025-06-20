package com.example.emaildemo.controller;


import com.example.emaildemo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String sendEmail(@RequestParam(required = false) String to,
                            @RequestParam(required = false) String subject,
                            @RequestParam(required = false) String body) {

        if (to == null || to.isBlank() || subject == null || subject.isBlank() || body == null || body.isBlank()) {
            return "❌ Missing required parameters: to, subject, body";
        }

        emailService.sendHtmlEmail(to, subject, body);
        return "✅ Email sent successfully";
    }
}
