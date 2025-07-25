package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDTO {
    private UUID senderId;
    private UUID receiverId;
    private String sentMessage;
    private LocalDateTime sentDateTime;
    private String senderName;
    private UUID messageId;



    private String avatarUrlSender;



    private String avatarUrlReceiver;
    // Getters & Setters
    public UUID getSenderId() {
        return senderId;
    }
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(String sentMessage) {
        this.sentMessage = sentMessage;
    }
    public LocalDateTime getSentDateTime() {
        return sentDateTime;
    }
    public void setSentDateTime(LocalDateTime sentDateTime) {
        this.sentDateTime = sentDateTime;
    }
    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public UUID getMessageId() {
        return messageId;
    }
    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }
    public String getAvatarUrlSender() {
        return avatarUrlSender;
    }

    public void setAvatarUrlSender(String avatarUrl) {
        this.avatarUrlSender = avatarUrl;
    }
    public String getAvatarUrlReceiver() {
        return avatarUrlReceiver;
    }

    public void setAvatarUrlReceiver(String avatarUrlReceiver) {
        this.avatarUrlReceiver = avatarUrlReceiver;
    }
}
