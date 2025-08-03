package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.DTOs.QuizDTO;
import com.example.Oboe.DTOs.QuizSearchResultDTO;
import com.example.Oboe.DTOs.QuizWithQuestionsDTO;
import com.example.Oboe.Entity.Quizzes;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;


    private final UserService userService;


    public QuizzesService(UserService userService) {

        this.userService = userService;

    }

    public List<QuizDTO> getAll() {
        return quizzesRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public List<QuizDTO> getAllByUserId(UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of(); // Trả về danh sách rỗng thay vì null
        List<Quizzes> quizzes = quizzesRepository.findQuizzesByUserId(userId);
        return quizzes.stream()
                .map(this::toDTO)
                .toList();
    }
    public QuizDTO getById(UUID id) {
        return quizzesRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Page<QuizDTO> getQuizzesByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Quizzes> quizzesPage = quizzesRepository.findQuizzesByUserIds(userId, pageable);
        List<QuizDTO> dtoList = quizzesPage.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, quizzesPage.getTotalElements());
    }
    public QuizDTO create(QuizDTO dto , UUID userId ) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;
        Quizzes quiz = new Quizzes();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setUser(userOpt.get());
        return toDTO(quizzesRepository.save(quiz));
    }

    public QuizDTO update(UUID id, QuizDTO dto, UUID userId) {
        Quizzes quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        //  Kiểm tra quyền sở hữu
        if (!quiz.getUser().getUser_id().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this quiz");
        }
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());

        return toDTO(quizzesRepository.save(quiz));
    }


    public void delete(UUID id, UUID userId) {
        Quizzes quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        //  Kiểm tra quyền sở hữu
        if (!quiz.getUser().getUser_id().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this quiz");
        }

        quizzesRepository.delete(quiz);
    }
    public QuizWithQuestionsDTO getQuizById(UUID id) {
        Quizzes quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizWithQuestionsDTO dto = new QuizWithQuestionsDTO();
        dto.setQuizzesID(quiz.getQuizzesID());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());



        List<QuestionDTO> questionDTOs = quiz.getQuestions().stream().map(q -> {
            QuestionDTO qDto = new QuestionDTO();
            qDto.setQuestionID(q.getQuestionID());
            qDto.setQuestionName(q.getQuestionName());
            qDto.setCorrectAnswer(q.getCorrectAnswer());



            // Làm sạch dữ liệu options
            String rawOptions = q.getOptions();
            List<String> cleanedOptions = new ArrayList<>();
            if (rawOptions != null && !rawOptions.isBlank()) {
                cleanedOptions = Arrays.stream(rawOptions
                                .replace("[", "")   // loại dấu [
                                .replace("]", "")   // loại dấu ]
                                .replace("\"", "")  // loại dấu "
                                .split(","))
                        .map(String::trim)         // loại bỏ khoảng trắng đầu/cuối
                        .filter(s -> !s.isEmpty()) // loại bỏ chuỗi rỗng
                        .toList();
            }
            qDto.setOptions(cleanedOptions);
            qDto.setQuizId(q.getQuiz().getQuizzesID());
            return qDto;
        }).toList();

        dto.setQuestions(questionDTOs);
        return dto;
    }



    // Convert Entity -> DTO
    private QuizDTO toDTO(Quizzes entity) {
        QuizDTO dto = new QuizDTO();
        dto.setQuizzesID(entity.getQuizzesID());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        //  Lấy UUID từ User entity
        if (entity.getUser() != null) {
            dto.setUserID(entity.getUser().getUser_id());
        }
        return dto;
    }



}
