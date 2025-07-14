package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    @PostMapping
    public QuestionDTO create(@RequestBody QuestionDTO dto) {
        return questionsService.create(dto);
    }

    @GetMapping("/by-quiz/{quizId}")
    public List<QuestionDTO> getByQuiz(@PathVariable UUID quizId) {
        return questionsService.getQuestionsByQuizId(quizId);
    }
}
