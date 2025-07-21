package com.example.emaildemo.entity_model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Users")
public class User {

    @Id
    private String id;  // ✅ Must be String for MongoDB

    private String username;
    private String password;
    private String email;

    // ✅ You already have lombok @Data for getters/setters
}
