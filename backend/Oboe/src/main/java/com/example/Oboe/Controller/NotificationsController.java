package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.NotificationsDTO;
import com.example.Oboe.Service.MessageService;
import com.example.Oboe.Service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;

    @Autowired
    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    // API: Lấy 30 thông báo mới nhất cho một user User_id được lấy qua token
    @GetMapping
    public ResponseEntity<List<NotificationsDTO>> getUserNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        List<NotificationsDTO> notifications = notificationsService.getAllNotification(userId);
        return ResponseEntity.ok(notifications);
    }
    //đánh dấu đã đọc
    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        int updatedCount = notificationsService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok("Đã đánh dấu " + updatedCount + " thông báo là đã đọc.");
    }
    @PutMapping("/read/{id}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable UUID id) {
        boolean updated = notificationsService.markNotificationAsRead(id);
        if (updated) {
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found or already read");
        }
    }

}
