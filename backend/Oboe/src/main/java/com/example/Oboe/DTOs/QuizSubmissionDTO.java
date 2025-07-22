package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class QuizSubmissionDTO {


    public List<UserAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<UserAnswerDTO> answers) {
        this.answers = answers;
    }


    private List<UserAnswerDTO> answers;
}
