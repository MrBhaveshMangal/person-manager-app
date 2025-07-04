package com.example.emaildemo.repository;

import com.example.emaildemo.entity_model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person,String> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByEmailAndUserId(String email, String userId); // new
    List<Person> findByUserId(String userId); // new
}
