package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.ActivityDTO;
import com.example.Oboe.DTOs.UserProfileDTO;
import com.example.Oboe.DTOs.UserProfileDTOwithStatistical;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.StatisticalUserSerivce;
import com.example.Oboe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private StatisticalUserSerivce statisticalUserSerivce;

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
        return ResponseEntity.ok(new UserProfileDTO(user));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTOwithStatistical> getUserProfile(@PathVariable UUID id) {
        UserProfileDTOwithStatistical userDto = userService.getUserByIds(id);
        return ResponseEntity.ok(userDto);
    }
    @GetMapping("/detail/{userId}")
    public ResponseEntity<Map<String, Object>> getUserActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "all") String type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityDTO> activityPage = statisticalUserSerivce.getUserActivities(userId, pageable);
        //tạo một đối tượng Map, trong đó (Key (khóa) là String (: "content", "page", "totalElements",...).
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", activityPage.getContent());
        response.put("page", activityPage.getNumber());
        response.put("size", activityPage.getSize());
        response.put("totalElements", activityPage.getTotalElements());
        response.put("totalPages", activityPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
