package com.example.emaildemo.startup;

import com.example.emaildemo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupEmailSender implements ApplicationRunner {

    @Autowired
    private EmailService emailService;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        emailService.sendHtmlEmail(
                "test@gmail.com",        //Receiver mail
                "App automatically start",                 // Subject
                "This email was sent as soon as the app start"//body
        );
    }
}
