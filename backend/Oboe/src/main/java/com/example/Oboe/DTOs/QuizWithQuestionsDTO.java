
package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class QuizWithQuestionsDTO {
    private UUID quizzesID;
    private String title;
    private String description;
    private List<QuestionDTO> questions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getQuizzesID() {
        return quizzesID;
    }

    public void setQuizzesID(UUID quizzesID) {
        this.quizzesID = quizzesID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }


}
