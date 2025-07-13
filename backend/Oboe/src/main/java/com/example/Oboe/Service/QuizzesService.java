package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuizDTO;
import com.example.Oboe.Entity.Quizzes;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;

    public List<QuizDTO> getAll() {
        return quizzesRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO getById(UUID id) {
        return quizzesRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public QuizDTO create(QuizDTO dto) {
        Quizzes quiz = new Quizzes();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        return toDTO(quizzesRepository.save(quiz));
    }

    // Convert Entity -> DTO
    private QuizDTO toDTO(Quizzes entity) {
        QuizDTO dto = new QuizDTO();
        dto.setQuizzesID(entity.getQuizzesID());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
