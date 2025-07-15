//package com.example.Oboe.Config;
//
//import com.example.Oboe.Util.JwtUtil;
//import com.example.Oboe.Service.UserService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    // Bean để xử lý sau khi đăng nhập thành công thông qua OAuth2
//    @Bean
//    public CustomOAuth2SuccessHandler customOAuth2SuccessHandler(UserService userService, JwtUtil jwtUtil , String domain) {
//        return new CustomOAuth2SuccessHandler(userService, jwtUtil, domain);
//    }
//
//    // Cấu hình bảo mật cho các API của ứng dụng
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http,
//                                           CustomOAuth2SuccessHandler successHandler) throws Exception {
//        http
//            // Cấu hình các quyền truy cập cho các endpoint
//            .authorizeHttpRequests(auth -> auth
//                // Cho phép truy cập công khai vào các endpoint liên quan đến login và signup
//                .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
//
//                // Cho phép truy cập công khai vào các endpoint /actuator/** và /error
//                .requestMatchers("/actuator/**", "/error").permitAll()
//
//                // Tất cả các endpoint còn lại yêu cầu phải xác thực
//                .anyRequest().authenticated()
//            )
//            // Cấu hình OAuth2 login để xử lý đăng nhập qua các dịch vụ OAuth2 (Google, Facebook, v.v.)
//            .oauth2Login(oauth -> oauth
//                .successHandler(successHandler)  // Định nghĩa cách xử lý khi người dùng đăng nhập thành công
//            )
//            // Tắt CSRF protection vì đây là API
//            .csrf(csrf -> csrf
//                .disable()  // Tắt CSRF cho các API, nếu không cần thiết
//            )
//            // Cấu hình CORS để hỗ trợ các yêu cầu từ các domain khác (nếu cần thiết)
//            .cors();  // Cấu hình CORS nếu cần thiết
//
//        // Trả về đối tượng SecurityFilterChain
//        return http.build();
//    }
//}
