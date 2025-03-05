package com.example.user_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.user_service.services.OAuthConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;


import java.util.Map;

@SpringBootApplication
public class UserServiceApplication {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue())); // Load vào System
    }

    public static void main(String[] args) {
    //     ApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);

    // // Lấy service từ ApplicationContext thay vì tự tạo new
    //     OAuthConfigService service = context.getBean(OAuthConfigService.class);

    //     try {
    //         Map<String, String> config = service.getOAuthConfig("google");
    //         System.out.println("OAuth Config: " + config);
    //     } catch (RuntimeException e) {
    //         System.out.println("Lỗi: " + e.getMessage());
    //     }
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
