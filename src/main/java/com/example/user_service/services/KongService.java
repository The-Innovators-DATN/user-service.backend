package com.example.user_service.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class KongService {

    private final RestTemplate restTemplate;

    public KongService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createKongConsumer(Long userId, String email) {
        String kongAdminUrl = "http://localhost:8001/consumers";
        String consumerUsername = "user-" + userId;

        String requestBody = "{ \"username\": \"" + consumerUsername + "\", \"custom_id\": \"" + email + "\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kongAdminUrl, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return extractConsumerIdFromJson(response.getBody());
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
            return root.path("id").asText();
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi parse JSON từ Kong: " + e.getMessage());
            return null;
        }
    }
}
