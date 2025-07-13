package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Questions;
import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionsRepository extends JpaRepository<Questions, UUID> {
    List<Questions> findByQuiz(Quizzes quiz);
}
