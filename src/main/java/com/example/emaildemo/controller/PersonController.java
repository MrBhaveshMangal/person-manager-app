package com.example.emaildemo.controller;

import com.example.emaildemo.entity_model.Person;
import com.example.emaildemo.repository.PersonRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addPerson(@Valid @RequestBody Person person){
        Optional<Person> existing = personRepository.findByEmail(person.getEmail());
        if(existing.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("Email already exist in the DB");
        }
        Person saved=personRepository.save(person);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Person>> getAllPersons(){
        List<Person> persons=personRepository.findAll();
        return ResponseEntity.ok(persons);
    }


    //update person
    @PutMapping("/update/{id}")
    public ResponseEntity<?>updatePerson(@PathVariable String id,@Valid @RequestBody Person updatedPerson){
        Optional<Person> optionalPerson=personRepository.findById(id);
        if(optionalPerson.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found with id"+ id);
        }

        Person existingPerson=optionalPerson.get();
        existingPerson.setName(updatedPerson.getName());
        existingPerson.setEmail(updatedPerson.getEmail());
        existingPerson.setDateOfBirth(updatedPerson.getDateOfBirth());

        return ResponseEntity.ok(personRepository.save(existingPerson));
    }

    //delete person
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable String id){
        if(!personRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found with id"+id);
        }
        personRepository.deleteById((id));
        return ResponseEntity.ok("Person with "+ id + " successfully deleted.");
    }


}
