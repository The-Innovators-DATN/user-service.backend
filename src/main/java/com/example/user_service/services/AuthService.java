package com.example.user_service.services;

import com.example.user_service.models.User;
import com.example.user_service.repositories.UserRepository;
import com.example.user_service.exceptions.EmailAlreadyExistsException;
import com.example.user_service.exceptions.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KongService kongService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, KongService kongService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kongService = kongService;
    }

    // Đăng ký user mới
    public User registerUser(String email, String fullName, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email đã tồn tại trong hệ thống: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setKongConsumerId(null);
        user = userRepository.save(user);

        String kongConsumerId = kongService.createKongConsumer(user.getId(), email);  // Mới
        if (kongConsumerId != null) {
            user.setKongConsumerId(UUID.fromString(kongConsumerId));
            userRepository.save(user);
        }

        return user;
    }

    // Phương thức authenticate để xác thực user
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Email không tồn tại: " + email));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Mật khẩu không chính xác");
        }

        return user;
    }
}
