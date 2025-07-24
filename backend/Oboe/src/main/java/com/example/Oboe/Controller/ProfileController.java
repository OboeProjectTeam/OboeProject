package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid principal");
        }

        String username = customUserDetails.getUsername();
        AuthProvider authProvider = customUserDetails.getAuthProvider();

        List<User> users = userService.findByUserNameAndAuthProvider(username, authProvider);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        if (users.size() > 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tìm thấy nhiều người dùng trùng username và provider.");
        }

        User user = users.get(0);
        user.setPassWord(null); // Ẩn mật khẩu
        return ResponseEntity.ok(user);
    }
}
