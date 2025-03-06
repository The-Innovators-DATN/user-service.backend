package com.example.user_service.controllers;

import com.example.user_service.config.JwtUtil;
import com.example.user_service.models.User;
import com.example.user_service.services.*;
import com.example.user_service.exceptions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ⚡ Fix lỗi: Inject thêm `AuthService`
    public UserController(UserService userService, AuthService authService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authService = authService;  // ✅ Inject authService
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
    
        return userService.authenticate(email, password)
                .map(user -> ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user.getKongConsumerId().toString())))) // ✅ Ép kiểu UUID -> String
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Unauthorized")));
    }
    

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String fullName = body.get("full_name");
        String password = body.get("password");

        try {
            User newUser = authService.registerUser(email, fullName, password);
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user_id", newUser.getId(),
                "kong_consumer_id", newUser.getKongConsumerId()
            ));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Email already registered",
                "message", e.getMessage()
            ));
        }
    }

    
}
