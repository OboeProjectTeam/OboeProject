package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.QuizSearchResultDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Quizzes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<Quizzes, UUID> {
    @Query("SELECT COUNT(q) FROM Quizzes q WHERE q.user.user_id = :userId")
    long countQuizzesByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM Quizzes b WHERE b.user.user_id = :userId")
    List<Quizzes> findQuizzesByUserId(UUID userId);

    //phân trang
    @Query("SELECT b FROM Quizzes b WHERE b.user.user_id = :userId")
    Page<Quizzes> findQuizzesByUserIds(@Param("userId") UUID userId, Pageable pageable);


    @Query(value = "SELECT * FROM quizzes ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Quizzes> findRandomQuizzes();

    @Query("SELECT new com.example.Oboe.DTOs.QuizSearchResultDTO(q.quizzesID, q.title, SIZE(q.questions), q.user.userName, q.user.avatarUrl) " +
            "FROM Quizzes q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<QuizSearchResultDTO> searchQuizzesByKeyword(@Param("keyword") String keyword,Pageable pageable);

    Optional<Quizzes> findById(UUID id);

}