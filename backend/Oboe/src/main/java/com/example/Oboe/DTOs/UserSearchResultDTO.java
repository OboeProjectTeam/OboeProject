package com.example.Oboe.DTOs;

import java.util.UUID;

public class UserSearchResultDTO {
    private UUID userId;
    private String userName;
    private Long flashcardCount;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    private String avatarUrl;

    public UserSearchResultDTO(UUID user_id, String userName, String avatarUrl, Long flashcardCount) {
        this.userId = user_id;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.flashcardCount = flashcardCount != null ? flashcardCount : 0;
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getFlashcardCount() {
        return flashcardCount;
    }

    public void setFlashcardCount(Long flashcardCount) {
        this.flashcardCount = flashcardCount;
    }
}