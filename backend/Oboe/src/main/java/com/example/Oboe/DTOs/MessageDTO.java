package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDTO {
    private String messageID;
    private String sent_message;
    private LocalDateTime sent_at;
    private UUID senderId;
    private UUID receiverId;
    private String senderUsername;      // Thêm mới
    private String receiverUsername;    // Thêm mới

    public MessageDTO(UUID messageID, String sent_message, LocalDateTime sent_at,
                      UUID senderId, UUID receiverId,
                      String senderUsername, String receiverUsername) {
        this.messageID = messageID.toString();
        this.sent_message = sent_message;
        this.sent_at = sent_at;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }


    public LocalDateTime getSent_at() {
        return sent_at;
    }

    public void setSent_at(LocalDateTime sent_at) {
        this.sent_at = sent_at;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSent_message() {
        return sent_message;
    }

    public void setSent_message(String sent_message) {
        this.sent_message = sent_message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


}
