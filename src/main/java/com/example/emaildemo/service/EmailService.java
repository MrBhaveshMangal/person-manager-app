package com.example.emaildemo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String toEmail,
                                String subject,
                                String htmlBody){
        MimeMessage mimeMessage=mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("bhaveshmangal.1210@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true => HTML content

            mailSender.send(mimeMessage);
            System.out.println("✅ HTML Email sent to " + toEmail);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send HTML Email: " + e.getMessage());
        }
    }
}
