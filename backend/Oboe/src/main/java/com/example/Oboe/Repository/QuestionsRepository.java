package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Questions;
import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionsRepository extends JpaRepository<Questions, UUID> {
    List<Questions> findByQuiz(Quizzes quiz);

    @Query("SELECT COUNT(q) FROM Questions q WHERE q.quiz.quizzesID = :quizId")
    int countByQuizId(@Param("quizId") UUID quizId);



}
