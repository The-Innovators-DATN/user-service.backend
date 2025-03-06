package com.example.user_service.services;

import com.example.user_service.models.User;
import com.example.user_service.models.UserSSO;
import com.example.user_service.repositories.UserRepository;
import com.example.user_service.repositories.UserSSORepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserSSORepository userSSORepository;

    public OAuth2SuccessHandler(UserRepository userRepository, UserSSORepository userSSORepository) {
        this.userRepository = userRepository;
        this.userSSORepository = userSSORepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String authProvider = request.getParameter("provider"); // Lấy provider từ query parameter
        String authId = oAuth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(oAuth2User.getAttribute("name"));
                    return userRepository.save(newUser);
                });

        Optional<UserSSO> existingSSO = userSSORepository.findByAuthId(authId);
        if (existingSSO.isEmpty()) {
            UserSSO newSSO = new UserSSO();
            newSSO.setUser(user);
            newSSO.setAuthProvider(authProvider);
            newSSO.setAuthId(authId);
            userSSORepository.save(newSSO);
        }

        String targetUrl = UriComponentsBuilder.fromUriString("https://your-frontend.com/dashboard")
                .queryParam("email", email)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
