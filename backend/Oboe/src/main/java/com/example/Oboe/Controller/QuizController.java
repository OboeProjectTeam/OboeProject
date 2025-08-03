package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.*;
import com.example.Oboe.Service.QuizzesService;
import com.example.Oboe.Service.UserAnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")

public class QuizController {

    @Autowired
    private QuizzesService quizzesService;
    @Autowired
    private UserAnswerService userAnswerService;

    @GetMapping
    public List<QuizDTO> getAll() {
        return quizzesService.getAll();
    }
    // get quizby id cả question
    @GetMapping("/user/{id}")
    public ResponseEntity<QuizWithQuestionsDTO> getQuizById(@PathVariable UUID id) {
        QuizWithQuestionsDTO quizDTO = quizService.getQuizById(id);
        return ResponseEntity.ok(quizDTO);
    }


    @GetMapping("/{id}")
    public QuizDTO getById(@PathVariable UUID id) {
        return quizzesService.getById(id);
    }
    @PostMapping
    public QuizDTO create(@RequestBody QuizDTO quizDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        return quizzesService.create(quizDTO, userId); //  chỉ gọi 1 lần, truyền đúng userId
    }
    @PutMapping("/{id}")
    public QuizDTO update(@PathVariable UUID id,
                          @RequestBody QuizDTO quizDTO,
                          Authentication authentication) {
        UUID userId = ((CustomUserDetails) authentication.getPrincipal()).getUserID();
        return quizzesService.update(id, quizDTO, userId); // truyền userId
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id,
                                         Authentication authentication) {
        UUID userId = ((CustomUserDetails) authentication.getPrincipal()).getUserID();
        quizzesService.delete(id, userId); // truyền userId
        return ResponseEntity.ok("Xóa Quizzes thành công");
    }
    @GetMapping("/user")
    public ResponseEntity<?> getQuizzesByUser(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        Page<QuizDTO> quizzesPage = quizzesService.getQuizzesByUser(userId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", quizzesPage.getContent());
        response.put("page", quizzesPage.getNumber());
        response.put("size", quizzesPage.getSize());
        response.put("totalElements", quizzesPage.getTotalElements());
        response.put("totalPages", quizzesPage.getTotalPages());
        response.put("last", quizzesPage.isLast());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/{quizId}/submit-answers")
    public ResponseEntity<?> submitAnswers(
            @PathVariable UUID quizId,
            @RequestBody QuizSubmissionDTO submission,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        QuizResultDTO resultDTO = userAnswerService.saveUserAnswer(submission.getAnswers(), userId, quizId);
        return ResponseEntity.ok(resultDTO);
    }



    @Autowired
    private QuizzesService quizService;



}
