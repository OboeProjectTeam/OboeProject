package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.ContactDTO;
import com.example.Oboe.DTOs.ConversationDTO;
import com.example.Oboe.DTOs.MessageDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Service.MessageService;
import com.example.Oboe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestParam UUID receiverId,
                                                  @RequestParam String content,
                                                  Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> senderOptional = userService.findByUserName(username);

            if (senderOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User sender = senderOptional.get();
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            Message message = messageService.sendMessage(sender, receiver, content);


            MessageDTO dto = new MessageDTO(
                    UUID.fromString(message.getMessageID().toString()),
                    message.getSent_message(),
                    message.getSent_at(),
                    message.getSender().getUser_id(),
                    message.getReceiver().getUser_id(),
                    message.getSender().getFirstName() + " " + message.getSender().getLastName(),
                    message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable UUID userId,
                                                            Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> currentUserOptional = userService.findByUserName(username);

            if (currentUserOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User currentUser = currentUserOptional.get();
            User otherUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<MessageDTO> conversation = messageService.getConversationDTO(currentUser, otherUser);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            System.out.println("Error getting conversation: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/contacts")
    public ResponseEntity<List<ContactDTO>> getContacts(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> currentUserOptional = userService.findByUserName(username);

            if (currentUserOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User currentUser = currentUserOptional.get();
            List<ContactDTO> contacts = messageService.getContactsDTO(currentUser);

            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            System.out.println("Error getting contacts: " + e.getMessage());
            e.printStackTrace(); // Thêm để debug chi tiết hơn
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id, Authentication authentication) {
        try {
            // ✅ Sửa tên method
            Message existingMessage = messageService.getMesseges(id);
            if (existingMessage == null) {
                return ResponseEntity.notFound().build();
            }

            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User currentUser = userOptional.get();

            // ✅ THÊM: Kiểm tra quyền sở hữu message
            if (!canDeleteMessage(existingMessage, currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            messageService.deleteMessage(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.out.println("Error deleting message: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ THÊM: Helper method kiểm tra quyền xóa
    private boolean canDeleteMessage(Message message, User user) {
        // User chỉ có thể xóa message mà họ gửi
        return message.getSender().getUser_id().equals(user.getUser_id());
    }
}