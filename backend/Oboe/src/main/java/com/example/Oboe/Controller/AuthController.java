package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.LoginRequest;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.UserService;
import com.example.Oboe.Util.JwtUtil;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTOs userDTOs) {
        if (userDTOs.getUserName() == null || userDTOs.getUserName().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        if (userService.findByUserName(userDTOs.getUserName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
        }

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

        Optional<UserDTOs> userOptional = userService.findByUserName(username)
                .map(user -> {
                    UserDTOs dto = new UserDTOs();
                    dto.setUserName(user.getUserName());
                    dto.setPassWord(user.getPassWord());
                    dto.setVerified(user.isVerified());
                    return dto;
                });

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserDTOs user = userOptional.get();

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify your email before logging in.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Raw password: " + password);
            System.out.println("Encoded password from DB: " + user.getPassWord());
            System.out.println("Match result: " + passwordEncoder.matches(password, user.getPassWord()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());


            Map<String, Object> body = new HashMap<>();
            body.put("message", "Login successful!");
            body.put("token", jwt);

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }
    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTOs userDTOs, Authentication authentication) {
        String username = authentication.getName();

        try {
            User updatedUser = userService.updateMyOwnProfile(username, userDTOs);
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
}
