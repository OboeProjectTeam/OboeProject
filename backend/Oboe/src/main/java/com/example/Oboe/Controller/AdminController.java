package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserProfileDTO;
import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.Status;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTOs dto) {
        try {
            User created = adminService.createUser(dto);
            if (created == null && dto.getRole() == Role.ROLE_USER) {
                return ResponseEntity.ok("Đã gửi email xác minh cho tài khoản người dùng.");
            }
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserDTOs dto) {
        try {
            return ResponseEntity.ok(adminService.updateUser(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
//        try {
//            adminService.deleteUser(id);
//            return ResponseEntity.ok("Xoá người dùng thành công.");
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.status(404).body("Không tìm thấy người dùng.");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
//        }
//    }
    @DeleteMapping("/delete/{userId}")
    public String deleteUser(@PathVariable UUID userId) {
        try {
            adminService.deleteUser(userId);
            return " Xóa user thành công!";
        } catch (Exception e) {
            e.printStackTrace();
            return " Lỗi khi xóa: " + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/change-role/{id}")
    public ResponseEntity<?> changeRole(@PathVariable UUID id, @RequestParam Role role) {
        try {
            return ResponseEntity.ok(adminService.changeRole(id, role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestParam Status status) {
        try {
            User updatedUser = adminService.updateStatus(id, status);
            return ResponseEntity.ok(new UserProfileDTO(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable UUID id, @RequestParam String newPassword) {
        try {
            return ResponseEntity.ok(adminService.resetPassword(id, newPassword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUserProfiles());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(adminService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchUsers(keyword));
    }
}
