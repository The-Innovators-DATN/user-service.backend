package com.example.user_service.repositories;


import com.example.user_service.models.SSOSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SSOSettingRepository extends JpaRepository<SSOSetting, Long> {
    Optional<SSOSetting> findByProvider(String provider);
}
