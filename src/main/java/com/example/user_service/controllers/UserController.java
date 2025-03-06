package com.example.user_service.controllers;

import com.example.user_service.config.JwtUtil;
import com.example.user_service.models.User;
import com.example.user_service.services.*;
import com.example.user_service.exceptions.EmailAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ⚡ Inject chỉ AuthService và JwtUtil vào Controller
    public UserController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // 📌 Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = authService.authenticate(email, password);

        if (user != null) {
            String token = jwtUtil.generateToken(user.getKongConsumerId().toString());
            String refreshToken = jwtUtil.generateRefreshToken(user.getKongConsumerId().toString());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login successful",
                    "data", Map.of(
                            "token", token,
                            "refresh_token", refreshToken
                    )
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid credentials"
            ));
        }
    }

    // 📌 Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");

        if (jwtUtil.isTokenValid(refreshToken)) {
            String consumerId = jwtUtil.decodeToken(refreshToken).getSubject();
            String newAccessToken = jwtUtil.generateToken(consumerId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Token refreshed successfully",
                    "data", Map.of("access_token", newAccessToken)
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired refresh token"
            ));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String fullName = body.get("full_name");
        String password = body.get("password");

        try {
            User newUser = authService.registerUser(email, fullName, password);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "User registered successfully",
                    "data", Map.of(
                            "user_id", newUser.getId(),
                            "kong_consumer_id", newUser.getKongConsumerId()
                    )
            ));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", "Email already registered",
                    "error_code", "email_exists"
            ));
        }
    }
}
