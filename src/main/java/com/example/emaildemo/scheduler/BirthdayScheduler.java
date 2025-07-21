package com.example.emaildemo.scheduler;

import com.example.emaildemo.entity_model.Person;
import com.example.emaildemo.repository.PersonRepository;
import com.example.emaildemo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BirthdayScheduler {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "*/20 * * * * *", zone = "Asia/Kolkata") // Every 5 seconds
    public void sendBirthdayEmails() {
        LocalDate today = LocalDate.now();
        System.out.println("📅 Running birthday check at: " + today);

        List<Person> people = personRepository.findAll();
        for (Person person : people) {
            try {
                String dobStr = person.getDateOfBirth().toString(); // Ensure format
                LocalDate dob = LocalDate.parse(dobStr);

                if (dob.getDayOfMonth() == today.getDayOfMonth() &&
                        dob.getMonth() == today.getMonth()) {

                    String htmlBody = "<h2 style='color:#007bff;'>🎉 Happy Birthday " + person.getName() + "!</h2>" +
                            "<p>Dear <b>" + person.getName() + "</b>, wishing you a great year ahead! 🎂🎈</p>";

                    emailService.sendHtmlEmail(
                            person.getEmail(),
                            "Happy Birthday " + person.getName(),
                            htmlBody
                    );

                    System.out.println("✅ Sent birthday email to: " + person.getEmail());
                }

            } catch (Exception e) {
                System.out.println("⚠️ Error for person: " + person.getName() + " - " + e.getMessage());
            }
        }
    }
}
