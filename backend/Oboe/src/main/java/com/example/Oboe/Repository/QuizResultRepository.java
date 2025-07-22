package com.example.Oboe.Repository;

import com.example.Oboe.Entity.QuizResults;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface QuizResultRepository extends JpaRepository<QuizResults, UUID> {

}

