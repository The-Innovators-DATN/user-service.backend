package com.example.user_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSSO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String authProvider; // google, github, microsoft

    @Column(nullable = false, unique = true)
    private String authId; // ID của user bên Google, GitHub

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getter + Setter
}
