package com.example.user_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")  // Đảm bảo đúng tên bảng
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;
    
    @Column(nullable = false)
    private String fullName;

    private Boolean isAdmin;

    @Column(columnDefinition = "UUID")  // ⚡ Đảm bảo là kiểu UUID
    private UUID kongConsumerId;

    @Column(nullable = false)
    private String passwordHash;
}
