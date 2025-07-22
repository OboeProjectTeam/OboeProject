package com.example.Oboe.DTOs;

import java.util.UUID;

public class UserAnswerDTO {
    private UUID questionId;
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }


}
