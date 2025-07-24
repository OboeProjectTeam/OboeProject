package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Questions;
import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionsRepository extends JpaRepository<Questions, UUID> {
    Page<Questions> findByQuiz(Quizzes quiz, Pageable pageable);

    int countByQuiz_QuizzesID(UUID quizzesId);





}
