package com.example.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class UserServiceApplication {
	
	// Autowire the Environment object for accessing environment variables
    @Autowired
    private Environment env;
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
