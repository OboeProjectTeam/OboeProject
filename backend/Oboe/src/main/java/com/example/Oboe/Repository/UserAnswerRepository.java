package com.example.Oboe.Repository;

import com.example.Oboe.Entity.UserAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswers, UUID> {


    @Query("SELECT MAX(u.attemptNumber) FROM UserAnswers u WHERE u.user.id = :userId AND u.quiz.id = :quizId")
    Integer findMaxAttemptNumber(@Param("userId") UUID userId, @Param("quizId") UUID quizId);



}
