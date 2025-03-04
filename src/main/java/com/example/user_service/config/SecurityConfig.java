package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.example.user_service.services.OAuth2UserService;
import com.example.user_service.config.JwtAuthenticationFilter;


import com.example.user_service.services.OAuth2SuccessHandler;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2UserService oAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))  // ✅ Đảm bảo dùng CustomOAuth2User
                .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Dùng BCrypt để hash password
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // @Bean
    // public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
    //     OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    //     successHandler.setPostLogoutRedirectUri("https://your-frontend.com/login");  // 🔥 Redirect user về frontend sau khi logout
    //     return successHandler;
    // }
}
