package com.example.emaildemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmaildemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmaildemoApplication.class, args);
	}
}
