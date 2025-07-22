package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<Quizzes, UUID> {

    @Query(value = "SELECT * FROM quizzes ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Quizzes> findRandomQuizzes();
}