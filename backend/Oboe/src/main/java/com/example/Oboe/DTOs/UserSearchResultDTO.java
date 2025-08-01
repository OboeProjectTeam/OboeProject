package com.example.Oboe.DTOs;

import java.util.UUID;

public class UserSearchResultDTO {
    private UUID userId;
    private String userName;
    private Long flashcardCount;

    public UserSearchResultDTO(UUID userId, String userName, Long flashcardCount) {
        this.userId = userId;
        this.userName = userName;
        this.flashcardCount = flashcardCount;
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
