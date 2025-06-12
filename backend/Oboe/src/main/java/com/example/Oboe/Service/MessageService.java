package com.example.Oboe.Service;

import com.example.Oboe.DTOs.ContactDTO;
import com.example.Oboe.DTOs.MessageDTO;
import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, String messageText) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSent_message(messageText);
        return messageRepository.save(message);
    }

    public List<MessageDTO> getConversationDTO(User user1, User user2) {
        List<Message> messages = messageRepository.findConversationBetweenUsers(user1, user2);
        return messages.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MessageDTO> getMessagesByUser(User user) {
        List<Message> messages = messageRepository.findBySenderOrReceiver(user, user);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(Message msg) {
        return new MessageDTO(
                UUID.fromString(msg.getMessageID().toString()),        // Đã là String, không cần .toString()
                msg.getSent_message(),
                msg.getSent_at(),
                msg.getSender().getUser_id(),
                msg.getReceiver().getUser_id(),
                msg.getSender().getFirstName() + " " + msg.getSender().getLastName(),
                msg.getReceiver().getFirstName() + " " + msg.getReceiver().getLastName()
        );
    }
    // Hoặc convert sang DTO đơn giản
    public List<ContactDTO> getContactsDTO(User currentUser) {
        List<User> users = messageRepository.findUsersWithMessages(currentUser);

        return users.stream()
                .map(user -> new ContactDTO(user.getUser_id(), user.getFirstName()+" "+user.getLastName()))
                .collect(Collectors.toList());
    }
    public Message getMesseges(UUID id) {
        return messageRepository.findById(id).orElse(null);
    }
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }
}