package com.example.user_service.services;

import com.example.user_service.models.SSOSetting;
import com.example.user_service.repositories.SSOSettingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OAuthConfigService {
    private final SSOSettingRepository ssoSettingRepository;
    private final ObjectMapper objectMapper;

    public OAuthConfigService(SSOSettingRepository ssoSettingRepository, ObjectMapper objectMapper) {
        this.ssoSettingRepository = ssoSettingRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getOAuthConfig(String provider) {
        Optional<SSOSetting> optionalSetting = ssoSettingRepository.findByProvider(provider);

        if (optionalSetting.isEmpty()) {
            throw new RuntimeException("Không tìm thấy config cho provider: " + provider);
        }

        try {
            return objectMapper.readValue(optionalSetting.get().getSettings(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi parse config OAuth2 từ DB", e);
        }
    }
}
