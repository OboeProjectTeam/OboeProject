package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.MessageDTO;
import com.example.Oboe.DTOs.UserSummaryDTO;
import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate; // ✅

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }


    // Gửi tin nhắn mới từ client đến server
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDto) {

        // Lưu tin nhắn vào database
        MessageDTO savedMessage = messageService.sendMessage(messageDto);

        // Gửi tin nhắn realtime đến người nhận (qua WebSocket),
        // Đây là phương thức gửi tin nhắn từ server → client thông qua WebSocket.

        messagingTemplate.convertAndSend(
                "/receiver/" + savedMessage.getReceiverId(),
                savedMessage
        );
        // Gửi thêm thông báo "Bạn có tin nhắn mới"
        String notification = "Bạn có tin nhắn mới từ " + savedMessage.getSenderName();
        messagingTemplate.convertAndSend(
                "/notification/" + savedMessage.getReceiverId(),
                notification
        );

        return ResponseEntity.ok(savedMessage);
    }



    @GetMapping("/partners")
    public ResponseEntity<List<UserSummaryDTO>> getChatPartners() {
        // Lấy thông tin user từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        return ResponseEntity.ok(messageService.getChatPartners(userId));
    }

    @GetMapping("/conversation/{userB}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable UUID userB) {
        UUID userId = ((CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUserID();
        List<MessageDTO> messages = messageService.getMessagesBetweenUsers(userId, userB);
        return ResponseEntity.ok(messages);
    }
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable UUID messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        try {
            boolean deleted = messageService.deleteMessage(messageId, userId);
            if (deleted) {
                return ResponseEntity.ok("Xóa tin nhắn thành công.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tin nhắn.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xóa tin nhắn này.");
        }
    }



}
