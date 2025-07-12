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

    public UserSummaryDTO(UUID userId, String firstName, String lastName, String userName,
                          String lastMessageContent, LocalDateTime lastMessageTime) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTime = lastMessageTime;
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
}
