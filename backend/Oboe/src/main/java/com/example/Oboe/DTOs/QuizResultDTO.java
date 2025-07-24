package com.example.Oboe.DTOs;

import java.time.LocalDateTime;

public class QuizResultDTO {
    private String message;
    private double score;
    private int totalQuestions;
    private long correctAnswers;
    private LocalDateTime takenAt;

    public QuizResultDTO(String message, double score, int totalQuestions, long correctAnswers, LocalDateTime takenAt) {
        this.message = message;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.takenAt = takenAt;
    }

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }
}
