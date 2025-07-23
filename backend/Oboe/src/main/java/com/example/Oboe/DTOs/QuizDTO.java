package com.example.Oboe.DTOs;

import java.util.UUID;

public class QuizDTO {
    private UUID quizzesID;
    private String title;
    private String description;
    private UUID UserID;

    public UUID getUserID() {
        return UserID;
    }

    public void setUserID(UUID userID) {
        UserID = userID;
    }



    // Getters & Setters
    public UUID getQuizzesID() {
        return quizzesID;
    }

    public void setQuizzesID(UUID quizzesID) {
        this.quizzesID = quizzesID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
