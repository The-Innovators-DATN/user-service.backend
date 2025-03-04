package com.example.user_service.services;

import com.example.user_service.models.User;
import com.example.user_service.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;  // 🆕 Dùng để gọi API Kong

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    public User registerUser(String email, String fullName, String rawPassword) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setKongConsumerId(null);  // ⚡ Ban đầu để null
        user = userRepository.save(user);
    
        // 🛠 Gọi Kong để lấy consumer_id
        String kongConsumerId = createKongConsumer(user.getId(), email);
        if (kongConsumerId != null) {
            user.setKongConsumerId(UUID.fromString(kongConsumerId)); // ✅ Convert String -> UUID
            userRepository.save(user);
        }
    
        return user;
    }    
    private String createKongConsumer(Long userId, String email) {
        String kongAdminUrl = "http://localhost:8001/consumers";  // ⚡ URL Kong Admin API
        String consumerUsername = "user-" + userId;  // Định danh trong Kong

        // Dữ liệu gửi đến Kong
        String requestBody = "{ \"username\": \"" + consumerUsername + "\", \"custom_id\": \"" + email + "\" }";

        // Cấu hình request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Gọi Kong API
            ResponseEntity<String> response = restTemplate.postForEntity(kongAdminUrl, requestEntity, String.class);

            // Parse response JSON để lấy consumer_id
            if (response.getStatusCode() == HttpStatus.CREATED) {
                String responseBody = response.getBody();
                assert responseBody != null;
                return extractConsumerIdFromJson(responseBody);  // Hàm parse JSON
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi tạo consumer ở Kong: " + e.getMessage());
        }

        return null;
    }

    private String extractConsumerIdFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            return root.path("id").asText();  // Lấy giá trị từ `id`
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi parse JSON từ Kong: " + e.getMessage());
            return null;
        }
    }
}
