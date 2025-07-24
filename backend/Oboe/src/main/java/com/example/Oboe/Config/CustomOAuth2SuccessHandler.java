package com.example.Oboe.Config;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.UserService;
import com.example.Oboe.Util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.domain}")
    private String domain;
    private final UserService userService;
    private final JwtUtil jwtUtil;


    public CustomOAuth2SuccessHandler(UserService userService, JwtUtil jwtUtil, String domain) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.domain = domain;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth = (OAuth2User) authentication.getPrincipal();

        String regId = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId()
                .toUpperCase(); // GOOGLE / FACEBOOK

        AuthProvider provider = AuthProvider.valueOf(regId);

        String providerId = oauth.getAttribute("sub") != null
                ? oauth.getAttribute("sub")
                : oauth.getAttribute("id");

        String email = oauth.getAttribute("email");
        String name = oauth.getAttribute("name") != null ? oauth.getAttribute("name") : "Unknown";

        System.out.println("OAuth2 Success - Provider: " + provider);
        System.out.println("OAuth2 Success - Provider ID: " + providerId);
        System.out.println("OAuth2 Success - Email: " + email);
        System.out.println("OAuth2 Success - Name: " + name);

        try {
            // Use email as username consistently for lookup and creation
            String username = email != null ? email : providerId;
            List<User> users = userService.findByUserNameAndAuthProvider(username, provider);
            User user;

            System.out.println("OAuth2 Success - Looking for user with username: " + username);
            System.out.println("OAuth2 Success - Found users count: " + users.size());

            if (users.isEmpty()) {
                String firstName = name.split(" ")[0];
                String lastName = name.contains(" ") ? name.substring(name.indexOf(' ') + 1) : "";

                UserDTOs dto = new UserDTOs();
                dto.setUserName(username);
                dto.setFirstName(firstName);
                dto.setLastName(lastName);
                dto.setVerified(true);
                dto.setAuthProvider(provider);
                dto.setProviderId(providerId);
                dto.setRole(Role.ROLE_USER);

                System.out.println("OAuth2 Success - Creating new user with username: " + dto.getUserName());
                user = userService.addUser(dto);
                System.out.println("OAuth2 Success - New user created with ID: " + user.getUser_id());
            } else if (users.size() == 1) {
                user = users.get(0);
                System.out.println("OAuth2 Success - Using existing user with ID: " + user.getUser_id());
            } else {
                throw new IllegalStateException("Tìm thấy nhiều tài khoản trùng providerId và provider.");
            }

            System.out.println("OAuth2 Success - Loading user details for: " + user.getUserName() + " with provider: " + provider);
            UserDetails principal = userService.loadUserByUsernameAndProvider(user.getUserName(), provider);
            String token = jwtUtil.generateToken(principal, provider.name());
            
            System.out.println("OAuth2 Success - Generated token: " + token.substring(0, Math.min(token.length(), 50)) + "...");

            String redirectUrl = domain + "/oauth2/redirect#token=" + token + "&provider=" + provider.name();
            System.out.println("OAuth2 Success - Redirecting to: " + redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (IllegalStateException e) {
            System.out.println("OAuth2 Success - Error: " + e.getMessage());
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(domain + "/login?error=" + errorMsg);
        }
        System.out.println("Redirecting to domain: " + domain);
    }
}
