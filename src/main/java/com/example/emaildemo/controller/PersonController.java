package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.Person;
import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.repository.PersonRepository;
import com.example.emaildemo.service.EmailService;
import com.example.emaildemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@CrossOrigin("*")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private String getCurrentUserId(Authentication auth) {
        return String.valueOf(userService.findByUsername(auth.getName()).getId());
    }

    // Add Person
    @PostMapping("/add")
    public ResponseEntity<?> addPerson(@Valid @RequestBody Person person, Authentication auth) {
        String userId = getCurrentUserId(auth);

        if (personRepository.findByEmailAndUserId(person.getEmail(), userId).isPresent()) {
            return ResponseEntity.status(409).body("Email already exists for this user");
        }

        person.setUserId(userId);
        Person saved = personRepository.save(person);

        // Send welcome email
        try {
            String htmlBody = "<h2 style='color:#007bff;'>Welcome " + saved.getName() + " to Person Manager!</h2>" +
                    "<p>We saved your birthday as <b>" + saved.getDateOfBirth() + "</b>. 🎂 We'll wish you on that day too!</p>";

            emailService.sendHtmlEmail(
                    saved.getEmail(),
                    "Welcome, " + saved.getName() + "!",
                    htmlBody
            );
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/me")
    public ResponseEntity<Person> getLoggedInPerson(Authentication auth) {
        String userId = getCurrentUserId(auth);
        return personRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/get")
    public ResponseEntity<List<Person>> getAllPersons(Authentication auth) {
        String userId = getCurrentUserId(auth);
        return ResponseEntity.ok(personRepository.findByUserId(userId));
    }

    // Update Person
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePerson(
            @PathVariable String id,
            @Valid @RequestBody Person updatedPerson,
            Authentication auth) {

        String userId = getCurrentUserId(auth);

        Person existingPerson = personRepository.findById(id)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Person not found for this user"));

        // Duplicate email check (excluding self)
        Optional<Person> emailConflict = personRepository.findByEmailAndUserId(updatedPerson.getEmail(), userId);
        if (emailConflict.isPresent() && !emailConflict.get().getId().equals(id)) {
            return ResponseEntity.status(409).body("Email already exists for this user");
        }

        existingPerson.setName(updatedPerson.getName());
        existingPerson.setEmail(updatedPerson.getEmail());
        existingPerson.setDateOfBirth(updatedPerson.getDateOfBirth());

        return ResponseEntity.ok(personRepository.save(existingPerson));
    }

    //  Delete Person
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable String id, Authentication auth) {
        String userId = getCurrentUserId(auth);

        Person existingPerson = personRepository.findById(id)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Person not found for this user"));

        personRepository.delete(existingPerson);
        return ResponseEntity.ok("Person deleted successfully.");
    }
}
