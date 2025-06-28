package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.Person;
import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.repository.PersonRepository;
import com.example.emaildemo.repository.UserRepository;
import com.example.emaildemo.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
@CrossOrigin("*")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService; // ✅ Inject EmailService

    @PostMapping("/add")
    public ResponseEntity<?> addPerson(@Valid @RequestBody Person person, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (personRepository.findByEmailAndUserId(person.getEmail(), user.getId()).isPresent()) {
            return ResponseEntity.status(409).body("Email already exists for this user");
        }

        person.setUserId(user.getId());
        Person saved = personRepository.save(person);

        // ✉️ Send welcome email after saving the person
        String htmlBody = "<h2 style='color:#007bff;'>Welcome " + saved.getName() + " to Person Manager!</h2>" +
                "<p>We saved your birthday as <b>" + saved.getDateOfBirth() + "</b>. 🎂 We'll wish you on that day too!</p>";

        emailService.sendHtmlEmail(
                saved.getEmail(),
                "Welcome, " + saved.getName() + "!",
                htmlBody
        );

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Person>> getAllPersons(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(personRepository.findByUserId(user.getId()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePerson(
            @PathVariable String id,
            @Valid @RequestBody Person updatedPerson,
            Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Person existingPerson = personRepository.findById(id)
                .filter(p -> p.getUserId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Person not found for this user"));

        existingPerson.setName(updatedPerson.getName());
        existingPerson.setEmail(updatedPerson.getEmail());
        existingPerson.setDateOfBirth(updatedPerson.getDateOfBirth());

        return ResponseEntity.ok(personRepository.save(existingPerson));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable String id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Person existingPerson = personRepository.findById(id)
                .filter(p -> p.getUserId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Person not found for this user"));

        personRepository.delete(existingPerson);
        return ResponseEntity.ok("Person deleted successfully.");
    }
}
