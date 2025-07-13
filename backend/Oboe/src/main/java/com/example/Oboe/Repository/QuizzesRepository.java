package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<Quizzes, UUID> {

}
