package com.example.Oboe.DTOs;

import java.util.UUID;

public class QuizSearchResultDTO {
    private UUID quizId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    private String title;
    private int questionCount;
    private String userName;
    private String avatarUrl;


    public QuizSearchResultDTO(UUID quizId, String title, int questionCount, String userName, String avatarUrl) {
        this.quizId = quizId;
        this.title = title;
        this.questionCount = questionCount;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }
}