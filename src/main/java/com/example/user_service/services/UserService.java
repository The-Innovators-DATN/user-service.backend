package com.example.user_service.services;

import com.example.user_service.models.User;
import com.example.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email);
    }
}
