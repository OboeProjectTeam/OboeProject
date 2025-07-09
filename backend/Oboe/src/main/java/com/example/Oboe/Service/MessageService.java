package com.example.Oboe.Service;


import com.example.Oboe.DTOs.MessageDTO;
import com.example.Oboe.DTOs.UserSummaryDTO;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.MessageRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;

    }
    public MessageDTO sendMessage(MessageDTO messageDto) {
        // Lấy người gửi
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Lấy người nhận
        User receiver = userRepository.findById(messageDto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Tạo đối tượng Message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSent_message(messageDto.getSentMessage());
        message.setSent_at(LocalDateTime.now());

        // Lưu và trả về DTO
        return toDTO(messageRepository.save(message));
    }


    public List<UserSummaryDTO> getChatPartners(UUID userId) {
        List<UUID> partnerIds = messageRepository.findAllPartnerIds(userId);

        List<User> users = userRepository.findByUserIdIn(partnerIds);

        return users.stream()
                .map(user -> new UserSummaryDTO(
                        user.getUser_id(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserName()
                ))
                .collect(Collectors.toList());
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