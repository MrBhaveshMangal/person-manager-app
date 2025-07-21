package com.example.emaildemo.repository;

import com.example.emaildemo.entity_model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends MongoRepository<Person, String> {
    List<Person> findByUserId(String userId);
    Optional<Person> findByEmailAndUserId(String email, String userId);
}
