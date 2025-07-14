package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class QuestionDTO {
    private UUID questionID;
    private String questionName;
    private String correctAnswer;
    private List<String> options; // <-- đổi thành List<String>// có thể là JSON array dạng string
    private UUID quizId;

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

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



    // Getters and Setters

}
