package com.example.user_service.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sso_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SSOSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String provider; // google, github

    @Column(nullable = false, columnDefinition = "TEXT")
    private String settings; // JSON lưu client_id, secret, redirect_uri

    private Boolean isDeleted = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getter + Setter
}
