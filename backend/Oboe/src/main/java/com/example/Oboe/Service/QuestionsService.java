package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Entity.Questions;
import com.example.Oboe.Entity.Quizzes;
import com.example.Oboe.Repository.QuestionsRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionsService {

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private QuizzesRepository quizzesRepository;

    public QuestionDTO create(QuestionDTO dto) {
        Quizzes quiz = quizzesRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Questions question = new Questions();
        question.setQuestionName(dto.getQuestionName());
        question.setCorrectAnswer(dto.getCorrectAnswer());

        // Sửa tại đây: convert List<String> -> String (để lưu vào DB)
        question.setOptions(String.join(";", dto.getOptions()));

        question.setQuiz(quiz);

        return toDTO(questionsRepository.save(question));
    }

    public List<QuestionDTO> getQuestionsByQuizId(UUID quizId) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        return questionsRepository.findByQuiz(quiz)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private QuestionDTO toDTO(Questions q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionID(q.getQuestionID());
        dto.setQuestionName(q.getQuestionName());
        dto.setCorrectAnswer(q.getCorrectAnswer());

        // Convert String to List<String>
        dto.setOptions(Arrays.asList(q.getOptions().split(";")));

        dto.setQuizId(q.getQuiz().getQuizzesID());
        return dto;
    }
}
