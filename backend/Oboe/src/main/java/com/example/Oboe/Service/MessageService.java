//package com.example.Oboe.Service;
//
//import com.example.Oboe.DTOs.ContactDTO;
//import com.example.Oboe.DTOs.MessageDTO;
//import com.example.Oboe.Entity.Message;
//import com.example.Oboe.Entity.MessageType;
//import com.example.Oboe.Entity.User;
//import com.example.Oboe.Repository.MessageRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class MessageService {
//
//    @Autowired
//    private MessageRepository messageRepository;
//
//    private final Path fileStorageLocation;
//
//    public MessageService() {
//        this.fileStorageLocation = Paths.get("uploads/messages")
//                .toAbsolutePath().normalize();
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
//
//    public Message sendTextMessage(User sender, User receiver, String messageText) {
//        Message message = new Message();
//        message.setSender(sender);
//        message.setReceiver(receiver);
//        message.setSent_message(messageText);
//        message.setMessageType(MessageType.TEXT);
//        return messageRepository.save(message);
//    }
//
//    public Message sendFileMessage(User sender, User receiver, MultipartFile file, String messageText) {
//        try {
//            // Generate unique filename
//            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//
//            // Create the file path
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//
//            // Save the file
//            Files.copy(file.getInputStream(), targetLocation);
//
//            // Create message
//            Message message = new Message();
//            message.setSender(sender);
//            message.setReceiver(receiver);
//            message.setSent_message(messageText);
//            message.setMessageType(file.getContentType().startsWith("image/") ? MessageType.IMAGE : MessageType.FILE);
//            message.setFileUrl("/uploads/messages/" + fileName);
//            message.setFileName(file.getOriginalFilename());
//            message.setFileSize(file.getSize());
//            message.setMimeType(file.getContentType());
//
//            return messageRepository.save(message);
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
//        }
//    }
//
//    public List<MessageDTO> getConversationDTO(User user1, User user2) {
//        List<Message> messages = messageRepository.findConversationBetweenUsers(user1, user2);
//        return messages.stream()
//                .map(this::convertToDTO)
//                .toList();
//    }
//
//    public List<MessageDTO> getMessagesByUser(User user) {
//        List<Message> messages = messageRepository.findBySenderOrReceiver(user, user);
//        return messages.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//
//
//    public List<ContactDTO> getContactsDTO(User currentUser) {
//        List<User> users = messageRepository.findUsersWithMessages(currentUser);
//
//        return users.stream()
//                .map(user -> new ContactDTO(user.getUser_id(), user.getFirstName()+" "+user.getLastName()))
//                .collect(Collectors.toList());
//    }
//
//    public Message getMesseges(UUID id) {
//        return messageRepository.findById(id).orElse(null);
//    }
//
//    public void deleteMessage(UUID id) {
//        Message message = messageRepository.findById(id).orElse(null);
//        if (message != null && (message.getMessageType() == MessageType.FILE || message.getMessageType() == MessageType.IMAGE)) {
//            try {
//                // Extract filename from fileUrl
//                String fileName = message.getFileUrl().substring(message.getFileUrl().lastIndexOf('/') + 1);
//                Path filePath = this.fileStorageLocation.resolve(fileName);
//                Files.deleteIfExists(filePath);
//            } catch (IOException e) {
//                // Log error but continue with message deletion
//                System.err.println("Could not delete file: " + e.getMessage());
//            }
//        }
//        messageRepository.deleteById(id);
//    }
//}