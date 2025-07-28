package com.example.Oboe.Config;

import com.example.Oboe.Config.CustomOAuth2SuccessHandler;
import com.example.Oboe.Service.UserService;
import com.example.Oboe.Util.JwtAuthencation;
import com.example.Oboe.Util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebConfig {

    private final JwtUtil jwtUtil;

    public WebConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Value("${app.domain}")
    private String domain;

    @Bean
    public CustomOAuth2SuccessHandler customOAuth2SuccessHandler(UserService userService) {
        return new CustomOAuth2SuccessHandler(userService, jwtUtil, domain);
    }

    @Bean
    public HttpFirewall allowUrlEncodedDoubleSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(HttpFirewall firewall) {
        return web -> web.httpFirewall(firewall);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthencation jwtAuthenticationFilter(UserService userService) {
        return new JwtAuthencation(userService, jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthencation jwtAuthenticationFilter,
                                                   UserService userService,
                                                   CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/verify",
                                "/swagger-ui/**",
                                "/oauth2/redirect",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/api/search/**",
                                "/api/search",
                                "/ws/**",
                                "/ws-raw"
                        ).permitAll()
                        // Chỉ cho phép GET cho các API sau mà không cần đăng nhập
                        .requestMatchers(HttpMethod.GET, "/api/kanji/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/grammar/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vocabulary/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sample-sentences/**").permitAll()
                        .requestMatchers("/ws/**", "/ws-raw").permitAll()
                        // Public cho Swagger
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()

                        // OAuth2 login
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // WebSocket
                        .requestMatchers("/ws/**").permitAll()

                        // Tất cả các request còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace();
                            String error = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                            response.sendRedirect(domain + "/login?error=" + error); // dùng domain luôn
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            if (auth != null) {
                                new SecurityContextLogoutHandler().logout(request, response, auth);
                            }

                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",  // local dev
                "http://localhost:5173",  // vite dev server default
                "http://localhost:5175",  // current vite dev server port
                "https://oboeru.me"       // production domain
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache preflight trong 1h

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
