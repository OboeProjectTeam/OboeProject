package com.example.Oboe.DTOs;

import java.util.UUID;

public class LearningMaterialDTO {
    private UUID quizId;
    private String title;
    private String description;

    public LearningMaterialDTO() {
    }

    public LearningMaterialDTO(UUID quizId, String title, String description) {
        this.quizId = quizId;
        this.title = title;
        this.description = description;
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
