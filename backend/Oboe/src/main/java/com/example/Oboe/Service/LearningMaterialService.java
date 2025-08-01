package com.example.Oboe.Service;

import com.example.Oboe.DTOs.LearningMaterialDTO;
import com.example.Oboe.Entity.Quizzes;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningMaterialService {

    private final QuizzesRepository quizzesRepository;

    @Autowired
    public LearningMaterialService(QuizzesRepository quizzesRepository) {
        this.quizzesRepository = quizzesRepository;
    }

    // Gợi ý từ 10 quiz đầu tiên, random 3 cái
    public List<LearningMaterialDTO> getSuggestedMaterials() {
        List<Quizzes> allQuizzes = quizzesRepository.findAll();

        List<Quizzes> top10 = allQuizzes.stream()
                .limit(10)
                .collect(Collectors.toList());

        Collections.shuffle(top10);

        return top10.stream()
                .limit(3)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Gợi ý random 3 quiz bất kỳ từ toàn bộ danh sách
    public List<LearningMaterialDTO> getSuggestedMaterialsRandom() {
        List<Quizzes> allQuizzes = quizzesRepository.findAll();

        // Nếu dưới 3 quiz thì trả về hết
        if (allQuizzes.size() <= 3) {
            return allQuizzes.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        // Shuffle toàn bộ và chọn 3 cái đầu
        Collections.shuffle(allQuizzes);

        return allQuizzes.stream()
                .limit(3)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private LearningMaterialDTO toDTO(Quizzes quiz) {
        LearningMaterialDTO dto = new LearningMaterialDTO();

        if (quiz.getUser() != null) {
            dto.setAuthor(quiz.getUser().getUserName());
            dto.setAvatarUrl(quiz.getUser().getAvatarUrl());
        } else {
            dto.setAuthor("Unknown");
            dto.setAvatarUrl(null);
        }

        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setQuizId(quiz.getQuizzesID());

        return dto;
    }

    public List<LearningMaterialDTO> getSuggestedMaterialsRandomFromDB() {
        List<Quizzes> randomQuizzes = quizzesRepository.findRandomQuizzes();

        return randomQuizzes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



}
