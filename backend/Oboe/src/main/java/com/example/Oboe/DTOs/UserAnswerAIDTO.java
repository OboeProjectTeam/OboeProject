package com.example.Oboe.DTOs;
import java.util.List;

public class UserAnswerAIDTO {

    private List<AnsweredQuestionDTO> answers;

    public List<AnsweredQuestionDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnsweredQuestionDTO> answers) {
        this.answers = answers;
    }
}
