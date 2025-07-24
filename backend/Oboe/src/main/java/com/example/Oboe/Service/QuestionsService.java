package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Entity.Questions;
import com.example.Oboe.Entity.Quizzes;
import com.example.Oboe.Repository.QuestionsRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    // đùng List để tạo được nhiều câu hỏi hơn thay vì gọi mỗi QuestionDTO tạo dc từng câu hỏi
    public List<QuestionDTO> create(List<QuestionDTO> dtoList) {

        List<QuestionDTO> createdQuestions = new ArrayList<>();

        for (QuestionDTO dto : dtoList) {
            Quizzes quiz = quizzesRepository.findById(dto.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + dto.getQuizId()));

            Questions question = new Questions();
            question.setQuestionName(dto.getQuestionName());
            question.setCorrectAnswer(dto.getCorrectAnswer());
            question.setOptions(String.join(";", dto.getOptions()));
            question.setQuiz(quiz);

            Questions saved = questionsRepository.save(question);
            createdQuestions.add(toDTO(saved));
        }

        return createdQuestions;
    }

    public Page<QuestionDTO> getQuestionsByQuizId(UUID quizId, Pageable pageable) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        return questionsRepository.findByQuiz(quiz, pageable)
                .map(this::toDTO); // chuyển Page<Questions> -> Page<QuestionDTO>
    }



    private QuestionDTO toDTO(Questions q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionID(q.getQuestionID());
        dto.setQuestionName(q.getQuestionName());
        dto.setCorrectAnswer(q.getCorrectAnswer());

        String optionsStr = q.getOptions().trim();

        if (optionsStr.startsWith("[") && optionsStr.endsWith("]")) {
            // Trường hợp bị lưu kiểu JSON string
            optionsStr = optionsStr.replaceAll("^\\[|\\]$", "")  // Bỏ [ và ]
                    .replaceAll("\"", "");        // Bỏ dấu "
            dto.setOptions(Arrays.asList(optionsStr.split(",")));
        } else {
            // Trường hợp bình thường: cách nhau bằng dấu ;
            dto.setOptions(Arrays.asList(optionsStr.split(";")));
        }

        dto.setQuizId(q.getQuiz().getQuizzesID());
        return dto;
    }

}
