package com.example.Oboe.Config;

import com.example.Oboe.Util.JwtUtil;
import com.example.Oboe.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public CustomOAuth2SuccessHandler customOAuth2SuccessHandler(UserService userService, JwtUtil jwtUtil) {
        return new CustomOAuth2SuccessHandler(userService, jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOAuth2SuccessHandler successHandler) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .successHandler(successHandler)
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
