package com.example.Oboe.Service;


import com.example.Oboe.DTOs.QuizResultDTO;
import com.example.Oboe.DTOs.UserAnswerDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserAnswerService {
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionsRepository questionsRepository;
    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;

    @Autowired
    public UserAnswerService(UserAnswerRepository userAnswerRepository, QuestionsRepository questionsRepository
            , QuizzesRepository quizzesRepository, UserRepository userRepository,QuizResultRepository quizResultRepository) {
        this.questionsRepository = questionsRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
        this.quizResultRepository =  quizResultRepository;
    }
    public QuizResultDTO saveUserAnswer(List<UserAnswerDTO> answers, UUID userId, UUID quizzesId) {
        // Lấy QuizID
        Quizzes quiz = quizzesRepository.findById(quizzesId).orElse(null);
        if (quiz == null) {
            return new QuizResultDTO("Không tìm thấy Quiz!", 0, 0, 0, LocalDateTime.now());
        }
        //Lấy Userid
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new QuizResultDTO("Không tìm thấy User!", 0, 0, 0, LocalDateTime.now());
        }

        // Đếm tổng số câu hỏi
        int totalQuestions = questionsRepository.countByQuiz_QuizzesID(quizzesId);


        if (totalQuestions == 0) {
            return new QuizResultDTO("Quiz không có câu hỏi nào!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }
        if (answers.size() < totalQuestions) {
            return new QuizResultDTO("Bạn chưa trả lời hết tất cả các câu hỏi trong quiz này!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }

        if (answers.size() > totalQuestions) {
            return new QuizResultDTO("Bạn đã trả lời nhiều hơn số câu hỏi trong quiz này!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }


        //  Tìm attemptNumber lớn nhất đã có
        Integer latestAttempt = userAnswerRepository.findMaxAttemptNumber(userId, quizzesId);
        int currentAttempt = (latestAttempt == null) ? 1 : latestAttempt + 1;

        long correctAnswers = 0;

        for (UserAnswerDTO dto : answers) {
            Questions question = questionsRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));

            // Kiểm tra đúng/sai
            boolean isCorrect = dto.getAnswer().equalsIgnoreCase(question.getCorrectAnswer());

            UserAnswers userAnswer = new UserAnswers();
            userAnswer.setQuiz(quiz);
            userAnswer.setUser(user);
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(dto.getAnswer());
            userAnswer.setCorrect(isCorrect);
            userAnswer.setAttemptNumber(currentAttempt);

            userAnswerRepository.save(userAnswer);

            if (isCorrect) correctAnswers++;
        }

        double score = ((double) correctAnswers / totalQuestions) * 100;
        score = Math.round(score * 100.0) / 100.0;
        LocalDateTime takenAt = LocalDateTime.now();

        QuizResults result = new QuizResults();
        result.setQuiz(quiz);
        result.setUser(user);
        result.setTakenAt(takenAt);
        result.setScore(score);
        try {
            quizResultRepository.save(result);
            return new QuizResultDTO("Lưu đáp án thành công!", score, totalQuestions, correctAnswers, takenAt);
        } catch (Exception e) {
            e.printStackTrace(); // hoặc log lỗi
            return new QuizResultDTO("Lỗi khi lưu kết quả quiz!", 0, totalQuestions, correctAnswers, LocalDateTime.now());
        }
    }
}





