package com.example.Oboe.DTOs;

import java.util.UUID;

public class ContactDTO {
    private UUID userId;
    private String username;

    public ContactDTO(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
