package com.example.user_service.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.load();  // Load .env file

    public static String get(String key) {
        return dotenv.get(key); // Fetch value from .env
    }
}
