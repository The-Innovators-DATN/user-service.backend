package com.example.user_service.repositories;


import com.example.user_service.models.SSOSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository
public interface SSOSettingRepository extends JpaRepository<SSOSetting, Long> {
    Optional<SSOSetting> findByProvider(String provider);
}
