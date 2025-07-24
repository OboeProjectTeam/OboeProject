package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSummaryDTO {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;


        private String avatarUrlReceiver;


    public UserSummaryDTO(UUID userId, String firstName, String lastName, String userName,
                          String lastMessageContent, LocalDateTime lastMessageTime, String avatarUrlReceiver) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTime = lastMessageTime;
        this.avatarUrlReceiver = avatarUrlReceiver;
    }

    //  Getters và Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    public String getAvatarUrlReceiver() {
        return avatarUrlReceiver;
    }

    public void setAvatarUrlReceiver(String avatarUrlReceiver) {
        this.avatarUrlReceiver = avatarUrlReceiver;
    }

}
