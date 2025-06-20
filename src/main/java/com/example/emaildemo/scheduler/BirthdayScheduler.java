package com.example.emaildemo.scheduler;

import com.example.emaildemo.entity_model.Person;
import com.example.emaildemo.repository.PersonRepository;
import com.example.emaildemo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BirthdayScheduler {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;

    private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "0 0 9 * * *")//Everyday at 9 AM
    public void sendBirthdayEmails(){
        LocalDate today=LocalDate.now();
        List<Person> people=personRepository.findAll();
        for(Person person:people){
            LocalDate dob=LocalDate.parse(person.getDateOfBirth(),formatter);

            if(dob.getDayOfMonth()==today.getDayOfMonth() && dob.getMonth()==today.getMonth()){
                String htmlBody = "<h2 style='color:#007bff;'>🎉 Happy Birthday " + person.getName() + "!</h2>" +
                        "<p style='font-size:16px;'>Dear <b>" + person.getName() + "</b>,</p>" +
                        "<p>Wishing you a <span style='color:#28a745;'>joyful</span> and <span style='color:#ff5733;'>successful</span> year ahead!</p>" +
                        "<p>🎂🎈 Enjoy your special day!</p>";


                emailService.sendHtmlEmail(
                        person.getEmail(),"Happy Birthday "+person.getName(),
                        htmlBody
                );
                System.out.println("Sent Birthday mail to: "+person.getEmail());
            }
        }
    }

}
