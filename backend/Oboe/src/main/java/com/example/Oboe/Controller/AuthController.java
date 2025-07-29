    package com.example.Oboe.Controller;
    import com.google.firebase.auth.FirebaseAuthException;
    import com.example.Oboe.DTOs.LoginRequest;
    import com.example.Oboe.DTOs.PassWordChangeDTOs;
    import com.example.Oboe.DTOs.UserDTOs;
    import com.example.Oboe.DTOs.FirebaseLoginRequest;
    import com.example.Oboe.Entity.AuthProvider;
    import com.example.Oboe.Entity.User;
    import com.example.Oboe.Service.UserService;
    import com.example.Oboe.Service.FirebaseService;
    import com.example.Oboe.Util.JwtUtil;
    import com.google.firebase.auth.FirebaseToken;
    import jakarta.validation.constraints.Email;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.*;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {
        private final UserService userService;
        private final AuthenticationManager authenticationManager;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final FirebaseService firebaseService;

        @Autowired
        public AuthController(UserService userService, AuthenticationManager authenticationManager, 
                            PasswordEncoder passwordEncoder, JwtUtil jwtUtil, FirebaseService firebaseService) {
            this.userService = userService;
            this.authenticationManager = authenticationManager;
            this.passwordEncoder = passwordEncoder;
            this.jwtUtil = jwtUtil;
            this.firebaseService = firebaseService;
        }


        @PostMapping("/signup")
        public ResponseEntity<?> signup(@RequestBody UserDTOs userDTOs) {
            if (userDTOs.getUserName() == null || userDTOs.getUserName().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required.");
            }

            if (userDTOs.getPassWord() == null || userDTOs.getPassWord().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required.");
            }

            AuthProvider currentProvider = userDTOs.getAuthProvider() != null ? userDTOs.getAuthProvider() : AuthProvider.EMAIL;
            List<User> existingUsers = userService.findByUserName(userDTOs.getUserName());

            for (User existing : existingUsers) {
                if (existing.getAuthProvider() == currentProvider) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Tài khoản đã tồn tại với nhà cung cấp " + currentProvider);
                }
            }

            // gửi mail xác thực
            userService.registerWithEmail(userDTOs);
            return ResponseEntity.ok("Verification email sent. Please check your email.");
        }



        @GetMapping("/verify")
        public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
            try {
                userService.verifyAccount(token);
                return ResponseEntity.ok("Account verified successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }


        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            String username = loginRequest.getUserName();
            String password = loginRequest.getPassWord();

            // Kiểm tra người dùng có tồn tại
            List<User> userList = userService.findByUserNameAndAuthProvider(username, AuthProvider.EMAIL);
            if (userList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            if (userList.size() > 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Nhiều tài khoản trùng username và provider.");
            }
            User user = userList.get(0);


            // Không cho đăng nhập nếu chưa xác minh
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Please verify your email before logging in.");
            }

            // Nếu là tài khoản Google/Facebook, không cho đăng nhập password
            if (user.getAuthProvider() != AuthProvider.EMAIL) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Hãy đăng nhập bằng " + user.getAuthProvider());
            }

            try {
                // Thực hiện xác thực Spring Security
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Lấy userDetails từ authentication và sinh JWT
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String jwt = jwtUtil.generateToken(userDetails, AuthProvider.EMAIL.name());

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("token", jwt);
                response.put("user", Map.of(
                        "username", user.getUserName(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "role", user.getRole().name(),
                        "displayName", user.getFirstName() + " " + user.getLastName(),
                        "avatarUrl", user.getAvatarUrl(), // Thêm avatarUrl
                        "photoURL", user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()
                            ? user.getAvatarUrl()
                            : "https://ui-avatars.com/api/?name=" + user.getFirstName() + "+" + user.getLastName()
                ));

                return ResponseEntity.ok(response);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
            }
        }

        @PutMapping("/updateProfile")
        public ResponseEntity<?> updateProfile(@RequestBody UserDTOs userDTOs, Authentication authentication) {
            String username = authentication.getName();

            try {
                User updatedUser = userService.updateMyOwnProfile(username,AuthProvider.EMAIL,userDTOs);
                return ResponseEntity.ok(updatedUser);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }


        @PutMapping("/changePassword")
        public ResponseEntity<?> changePassword(@RequestBody PassWordChangeDTOs passwordChange,
                                                Authentication authentication) {
            String username = authentication.getName();

            try {
                userService.changePassword(username, passwordChange);
                return ResponseEntity.ok("Password changed successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
            }
        }

        @DeleteMapping("/users/{id}")
        public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
            userService.deleteUser(id);
            return ResponseEntity.ok("Tài khoản và dữ liệu liên quan đã được xóa.");
        }


        @PostMapping("/uploadAvatar")
        public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                              Authentication authentication) {
            String username = authentication.getName();

            try {
                User user = userService.uploadAvatarForUser(username, AuthProvider.EMAIL, file);
                return ResponseEntity.ok(Map.of("avatarUrl", user.getAvatarUrl()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
            }
        }
       @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu Authorization header");
            }

            String token = authHeader.substring(7); // Bỏ "Bearer "

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            // Lấy username (sub) và provider từ JWT
            String username = jwtUtil.getUsernameFromToken(token);
            String providerStr = jwtUtil.getProviderFromToken(token);

            AuthProvider provider;
            try {
                provider = AuthProvider.valueOf(providerStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provider không hợp lệ: " + providerStr);
            }

            User user = userService.findByUserNameAndAuthProvider(username, provider)
                    .stream().findFirst().orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy user với username = " + username + " và provider = " + provider);
            }

            Map<String, Object> response = Map.of(
                    "user", Map.of(
                            "username", user.getUserName(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "role", user.getRole().name(),
                            "displayName", user.getFirstName() + " " + user.getLastName(),
                            "avatarUrl", user.getAvatarUrl(), // Thêm avatarUrl
                            "photoURL", user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()
                                ? user.getAvatarUrl()
                                : "https://ui-avatars.com/api/?name=" + user.getFirstName() + "+" + user.getLastName()
                                )
            );

            return ResponseEntity.ok(response);
        }

        @PostMapping("/loginWithFirebase")
        public ResponseEntity<?> loginWithFirebase(@RequestBody FirebaseLoginRequest request) {
           
            if (request == null || request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
                System.out.println("ERROR: Missing or empty ID token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing or empty Firebase ID token");
            }
            
            try {
                FirebaseToken decodedToken = firebaseService.verifyIdToken(request.getIdToken());
                User user = firebaseService.processFirebaseUser(decodedToken);
                UserDetails userDetails = userService.loadUserByUsernameAndProvider(
                    user.getUserName(), user.getAuthProvider());
                String jwt = jwtUtil.generateToken(userDetails, user.getAuthProvider().name());
                // Prepare response
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Firebase login successful!");
                response.put("token", jwt);
                response.put("user", Map.of(
                        "username", user.getUserName(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "role", user.getRole().name(),
                        "displayName", user.getFirstName() + " " + user.getLastName(),
                        "avatarUrl", user.getAvatarUrl(), // Thêm avatarUrl
                        "photoURL", user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()
                            ? user.getAvatarUrl()
                            : "https://ui-avatars.com/api/?name=" + user.getFirstName() + "+" + user.getLastName()
                ));

                return ResponseEntity.ok(response);
                
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Firebase token: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("ERROR: General Exception - " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase authentication failed: " + e.getMessage());
            }
        }

    }
