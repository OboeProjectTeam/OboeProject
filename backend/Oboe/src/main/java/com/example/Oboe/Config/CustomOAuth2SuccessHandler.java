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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public CustomOAuth2SuccessHandler(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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

        try {
            List<User> users = userService.findByUserNameAndAuthProvider(providerId, provider);
            User user;

            if (users.isEmpty()) {
                String firstName = name.split(" ")[0];
                String lastName = name.contains(" ") ? name.substring(name.indexOf(' ') + 1) : "";

                UserDTOs dto = new UserDTOs();
                dto.setUserName(email != null ? email : providerId); // Lưu username là email nếu có
                dto.setFirstName(firstName);
                dto.setLastName(lastName);
                dto.setVerified(true);
                dto.setAuthProvider(provider);
                dto.setProviderId(providerId);
                dto.setRole(Role.ROLE_USER);

                user = userService.addUser(dto);
            } else if (users.size() == 1) {
                user = users.get(0);
            } else {
                throw new IllegalStateException("Tìm thấy nhiều tài khoản trùng providerId và provider.");
            }

            UserDetails principal = userService.loadUserByUsernameAndProvider(user.getUserName(), provider);
            String token = jwtUtil.generateToken(principal, provider.name());

            String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + token + "&provider=" + provider.name();
            response.sendRedirect(redirectUrl);

        } catch (IllegalStateException e) {
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:3000/login?error=" + errorMsg);
        }
    }
}
