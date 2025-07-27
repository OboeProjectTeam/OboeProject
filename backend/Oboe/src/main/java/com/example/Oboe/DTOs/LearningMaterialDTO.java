package com.example.Oboe.DTOs;

import java.util.UUID;

public class LearningMaterialDTO {
    private UUID quizId;
    private String title;
    private String description;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String author;
    private String avatarUrl;

    public LearningMaterialDTO() {
    }

    public LearningMaterialDTO(UUID quizId, String title, String description , String author , String avatarUrl) {
        this.quizId = quizId;
        this.title = title;
        this.description = description;
        this.author = author;
        this.avatarUrl = avatarUrl;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
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

    @Override
    public String toString() {
        return "LearningMaterialDTO{" +
                "quizId=" + quizId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
