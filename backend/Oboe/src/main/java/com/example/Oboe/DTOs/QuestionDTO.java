package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class QuestionDTO {
    private UUID questionID;
    private String questionName; // Tên câu hỏi (từ "question" của JSON)
    private String correctAnswer; // Đáp án đúng (từ "answer" của JSON)
    private List<String> options; // Danh sách lựa chọn (từ "choices" của JSON)
    private UUID quizId;

    // === Getters và Setters ===

    public UUID getQuestionID() {
        return questionID;
    }

    public void setQuestionID(UUID questionID) {
        this.questionID = questionID;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }
}
