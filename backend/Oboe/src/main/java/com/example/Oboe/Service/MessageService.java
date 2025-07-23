    package com.example.Oboe.Service;


    import com.example.Oboe.DTOs.MessageDTO;
    import com.example.Oboe.DTOs.UserSummaryDTO;
    import com.example.Oboe.Entity.Comment;
    import com.example.Oboe.Entity.Message;
    import com.example.Oboe.Entity.Notifications;
    import com.example.Oboe.Entity.User;
    import com.example.Oboe.Repository.CommentRepository;
    import com.example.Oboe.Repository.MessageRepository;
    import com.example.Oboe.Repository.NotificationsRepository;
    import com.example.Oboe.Repository.UserRepository;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;

    import javax.management.Notification;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    public class
    MessageService {


        private final MessageRepository messageRepository;
        private final UserRepository userRepository;
        private final NotificationsRepository notificationsRepository;
        private final SimpMessagingTemplate messagingTemplate;
        public MessageService(MessageRepository messageRepository, UserRepository userRepository,NotificationsRepository notificationsRepository, SimpMessagingTemplate messagingTemplate) {

            this.messageRepository = messageRepository;
            this.userRepository = userRepository;
            this.notificationsRepository = notificationsRepository;
            this.messagingTemplate = messagingTemplate;
        }
        public MessageDTO sendMessage(UUID senderId, MessageDTO messageDto) {
            // Lấy người gửi từ token
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            // Lấy người nhận từ DTO
            User receiver = userRepository.findById(messageDto.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Tạo và lưu message
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setSent_message(messageDto.getSentMessage());
            ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");
            LocalDateTime localDateTimeVN = LocalDateTime.now(zoneVN);
            message.setSent_at(localDateTimeVN);

            Message savedMessage = messageRepository.save(message);

            // Tạo thông báo
            Notifications notification = new Notifications();
            notification.setUser(receiver);
            notification.setText_notification("Bạn nhận được một tin nhắn mới từ " + sender.getUserName());
            notification.setRead(false);
            notification.setUpdate_at(LocalDateTime.now());

            notificationsRepository.save(notification);

            // Gửi WebSocket đến client
            MessageDTO dto = toDTO(savedMessage);
            messagingTemplate.convertAndSend("/receiver/" + receiver.getUser_id(), dto);
            messagingTemplate.convertAndSend("/notification/" + receiver.getUser_id(), notification.getText_notification());

            return dto;
        }



        public List<UserSummaryDTO> getChatPartners(UUID userId) {
            List<UUID> partnerIds = messageRepository.findAllPartnerIds(userId);
            List<User> users = userRepository.findByUserIdIn(partnerIds);
            Pageable limitOne = PageRequest.of(0, 1);

            return users.stream().map(user -> {
                Message lastMsg = messageRepository
                        .findMessageNew(userId, user.getUser_id(), limitOne)
                        .stream().findFirst().orElse(null);

                return new UserSummaryDTO(
                        user.getUser_id(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserName(),
                        lastMsg != null ? lastMsg.getSent_message() : null,
                        lastMsg != null ? lastMsg.getSent_at() : null
                );
            }).collect(Collectors.toList());
        }

        //lấy tất cả cuộc hội thoại
        public List<MessageDTO> getMessagesBetweenUsers(UUID userA, UUID userB) {
            Pageable top30 = PageRequest.of(0, 30); // chỉ lấy 30 tin mới nhất
            List<Message> messages = messageRepository.findConversation(userA, userB,top30);

            Collections.reverse(messages); //  chuyển tin nhắn từ  cũ sang mới

            return messages.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        public boolean deleteMessage(UUID messageId, UUID userId) {
            Message message = getMessage(messageId);
            // Kiểm tra quyền: chỉ sender mới được xóa
            if (!message.getSender().getUser_id().equals(userId)) {
                return false;
            }
            messageRepository.delete(message);
            return true;
        }

        public Message getMessage(UUID messageId) {
            return messageRepository.findById(messageId).orElse(null);
        }

        private MessageDTO toDTO(Message message) {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(message.getMessageID());
            dto.setSenderId(message.getSender().getUser_id());
            dto.setReceiverId(message.getReceiver().getUser_id());
            dto.setSentMessage(message.getSent_message());
            dto.setSentDateTime(message.getSent_at());
            dto.setSenderName(message.getSender().getUserName());
            return dto;
        }









    }